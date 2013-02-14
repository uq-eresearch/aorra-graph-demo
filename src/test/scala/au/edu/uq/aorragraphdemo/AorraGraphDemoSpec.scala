package au.edu.uq.aorragraphdemo

import org.scalatra.test.scalatest._

class AorraGraphDemoSpec extends ScalatraSpec {

  addFilter(classOf[AorraGraphDemo], "/*")

  describe("Demo Application") {
    describe("GET /") {
      it("should return status 200") {
        get("/") {
          status should equal (200)
        }
      }
    }

    describe("GET /progress-chart") {
      it("should return status 400") {
        get("/progress-chart") {
          status should equal (400)
        }
      }

      describe("with value=42") {
        it("should return status 200") {
          get("/progress-chart?value=42") {
            status should equal (200)
          }
        }
      }
    }

  }

}
