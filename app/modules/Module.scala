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

package modules

import javax.inject.Singleton

import com.google.inject.{AbstractModule, Provides}
import config.{MicroserviceAuditConnector, WSHttp}
import connectors._
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.DefaultDB
import uk.gov.hmrc.http.{CorePost, CorePut}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[DeregisterSubscriptionConnector]).toInstance(DESConnector)
    bind(classOf[WithdrawSubscriptionConnector]).toInstance(DESConnector)
    bind(classOf[PayAPIConnector]).toInstance(PayAPIConnector)
    bind(classOf[GovernmentGatewayAdminConnector]).toInstance(GovernmentGatewayAdminConnector)
    bind(classOf[SubscribeDESConnector]).toInstance(DESConnector)
    bind(classOf[CorePost]).toInstance(WSHttp)
    bind(classOf[CorePut]).toInstance(WSHttp)
    bind(classOf[AuditConnector]).toInstance(MicroserviceAuditConnector)
  }

  @Provides
  @Singleton
  def mongoDB(reactiveMongoComponent: ReactiveMongoComponent): () => DefaultDB = reactiveMongoComponent.mongoConnector.db
}
