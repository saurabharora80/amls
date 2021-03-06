/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package repositories

import models.Fees
import play.api.Logger
import play.api.libs.json.Json
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.DefaultDB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait FeesRepository extends Repository[Fees, BSONObjectID] {

  def insert(feeResponse: Fees):Future[Boolean]

  def findLatestByAmlsReference(amlsReferenceNumber: String):Future[Option[Fees]]

}

class FeesMongoRepository()(implicit mongo: () => DefaultDB) extends ReactiveRepository[Fees, BSONObjectID]("fees", mongo, Fees.format)
  with FeesRepository{



  override def indexes: Seq[Index] = {
    import reactivemongo.bson.DefaultBSONHandlers._

      Seq(Index(Seq("createdAt" -> IndexType.Ascending), name = Some("feeResponseExpiry"),
          options = BSONDocument("expireAfterSeconds" -> 2592000)))


  }

  override def insert(feeResponse: Fees):Future[Boolean] = {
    collection.insert[Fees](feeResponse) map { lastError =>
      Logger.debug(s"[FeeResponseMongoRepository][insert] : { feeResponse : $feeResponse , result: ${lastError.ok}, errors: ${lastError.errmsg} }")
      lastError.ok
    }
  }

  override def findLatestByAmlsReference(amlsReferenceNumber: String) = {
    collection.find(Json.obj("amlsReferenceNumber" -> amlsReferenceNumber)).sort(Json.obj("createdAt" -> -1)).one[Fees]
  }
}

object FeesRepository extends MongoDbConnection {

  private lazy val feesRepository = new FeesMongoRepository

  def apply(): FeesMongoRepository = feesRepository
}
