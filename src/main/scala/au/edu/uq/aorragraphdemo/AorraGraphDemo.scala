package au.edu.uq.aorragraphdemo

import org.scalatra._
import scalate.ScalateSupport
import java.io.File
import javax.servlet.http.HttpServletRequest

class AorraGraphDemo extends ScalatraFilter with ScalateSupport {

  get("/") {
    <html><head></head><body><p>Placeholder</p></body></html>
  }

}
