// (c) Copyright [2020-2021] Micro Focus or one of its affiliates.
// Licensed under the Apache License, Version 2.0 (the "License");
// You may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.vertica.spark.datasource.core

import ch.qos.logback.classic.Level
import com.vertica.spark.config._
import com.vertica.spark.datasource.fs.HadoopFileStoreLayer
import com.vertica.spark.datasource.jdbc.VerticaJdbcLayer
import com.vertica.spark.util.schema.SchemaTools

/**
 * Factory for creating a data pipe to send or retrieve data from Vertica
 *
 * Constructed based on the passed in configuration
 */
trait VerticaPipeFactoryInterface {
  def getReadPipe(config: ReadConfig): VerticaPipeInterface with VerticaPipeReadInterface

  def getWritePipe(config: WriteConfig): VerticaPipeInterface with VerticaPipeWriteInterface = ???
}

/**
 * Implementation of the vertica pipe factory
 */
object VerticaPipeFactory extends VerticaPipeFactoryInterface{
  override def getReadPipe(config: ReadConfig): VerticaPipeInterface with VerticaPipeReadInterface = {
    config match {
      case cfg: DistributedFilesystemReadConfig =>
        val hadoopFileStoreLayer =  new HadoopFileStoreLayer(cfg.logProvider, cfg.metadata match {
          case Some(metadata) => Some(metadata.schema)
          case None => None
        })
        new VerticaDistributedFilesystemReadPipe(cfg, hadoopFileStoreLayer, new VerticaJdbcLayer(cfg.jdbcConfig), new SchemaTools(cfg.logProvider))
    }
  }
  override def getWritePipe(config: WriteConfig): VerticaPipeInterface with VerticaPipeWriteInterface = ???
}

