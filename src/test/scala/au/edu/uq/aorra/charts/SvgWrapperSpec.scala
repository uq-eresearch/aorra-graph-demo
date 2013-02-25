package au.edu.uq.aorra.charts

import org.jfree.chart.renderer.category.GroupedStackedBarRenderer
import org.scalatest.FunSpec
import org.scalamock.scalatest.MockFactory
import org.apache.batik.dom.svg.SVGDOMImplementation
import org.w3c.dom.svg.SVGDocument
import org.apache.batik.svggen.SVGGraphics2D
import java.io.CharArrayWriter
import org.apache.batik.svggen.DefaultImageHandler
import org.apache.batik.svggen.DefaultExtensionHandler

import org.scalatest.matchers.ShouldMatchers._

class SvgWrapperSpec extends FunSpec with MockFactory {

  describe("SVG Wrapper") {
    describe("supported formats") {
      Seq("pdf","png","svg","tiff").foreach { f =>
          it(s"should support ${f.toUpperCase}") {
            val pdfDoc = SvgWrapper(testSvg).toFormat(f)
            pdfDoc.output should be ('defined)
          }
      }

      lazy val testSvg = {
        // Get a DOMImplementation.
        val impl = SVGDOMImplementation.getDOMImplementation()
        val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
        val doc = impl.createDocument(svgNS, "svg", null).asInstanceOf[SVGDocument]

        // Create an instance of the SVG Generator.
        val g2 = new SVGGraphics2D(doc,
          new DefaultImageHandler(),
          new DefaultExtensionHandler(),
          true)

        g2.fill3DRect(10, 10, 10, 10, true);

        val cw = new CharArrayWriter()
        g2.stream(cw, true)
        g2.dispose

        cw.toString()
      }
    }
  }

}