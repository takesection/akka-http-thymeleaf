package com.pigumer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

trait Hello {

  def createActorSystem = ActorSystem("example")

  implicit val system = createActorSystem
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  private val templateEngine = {
    val cl = Thread.currentThread.getContextClassLoader
    val engine = new TemplateEngine()
    val resolver = new ClassLoaderTemplateResolver(cl)
    resolver.setPrefix("/templates/")
    engine.setTemplateResolver(resolver)
    engine
  }

  def route = pathEndOrSingleSlash {
    get {
      complete {
        val context = new Context()
        HttpEntity(ContentTypes.`text/html(UTF-8)`, templateEngine.process("hello.html", context))
      }
    }
  }

  def bindAndHandle = Http().bindAndHandle(route, "0.0.0.0", 8080)
}

object Hello extends App with Hello {
  bindAndHandle
}
