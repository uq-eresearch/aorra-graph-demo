package au.edu.uq.aorra.charts

import org.apache.batik.dom.svg.SAXSVGDocumentFactory
import org.apache.batik.util.XMLResourceDescriptor
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.batik.transcoder.{ TranscoderInput, TranscoderOutput }
import org.apache.batik.transcoder.svg2svg.SVGTranscoder
import java.io.ByteArrayOutputStream
import java.io.StringWriter
import java.io.CharArrayReader
import org.apache.batik.gvt.renderer.StaticRenderer
import java.awt.RenderingHints
import java.lang.reflect.Field
import org.apache.batik.transcoder.SVGAbstractTranscoder
import org.apache.fop.svg.PDFTranscoder
import org.apache.fop.render.ps.EPSTranscoder
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.apache.batik.transcoder.Transcoder
import org.w3c.dom.Document

object SvgWrapper {
  def apply(svg: String) = new SvgWrapper(svg, None, None)
  def apply(svg: String, width: Option[Int], height: Option[Int]) = {
    new SvgWrapper(svg, width, height)
  }

  abstract class ImageFormat {
    def output: Option[Any]
    def mimetype: Option[String]
  }
  case class UnsupportedFormat extends ImageFormat {
    def output() = None
    def mimetype: Option[String] = None
  }

}

class SvgWrapper(svg: String, width: Option[Int], height: Option[Int]) {
  import SvgWrapper._

  case class RasterFormat(
    val m: String,
    val d: Document,
    val t: Transcoder) extends ImageFormat {
    def mimetype = Some(m)
    def output() = {
      val os = new ByteArrayOutputStream()
      t.transcode(new TranscoderInput(d), new TranscoderOutput(os))
      Some(os.toByteArray())
    }

  }
  case class VectorFormat(
    val m: String,
    val d: Document,
    val t: Transcoder) extends ImageFormat {
    def mimetype = Some(m)
    def output() = {
      val w = new StringWriter()
      t.transcode(new TranscoderInput(d), new TranscoderOutput(w))
      Some(w.toString())
    }
  }

  def toFormat(format: String): ImageFormat = format match {
    case "eps" =>
      VectorFormat("application/postscript", doc(true), new EPSTranscoder())
    case "pdf" =>
      VectorFormat("application/pdf", doc(true), new PDFTranscoder())
    case "svg" =>
      VectorFormat("image/svg+xml", doc(true), new SVGTranscoder())
    case "jpg" =>
      RasterFormat("image/jpeg", doc(false),
        withDimensions(new JPEGTranscoder()))
    case "png" =>
      RasterFormat("image/png", doc(false),
        withDimensions(new PNGTranscoder()))
    case _ => UnsupportedFormat()
  }

  private def withDimensions(t: Transcoder) = {
    width.foreach { w =>
      t.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, w.toFloat)
    }
    height.foreach { h =>
      t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, h.toFloat)
    }
    t
  }

  def toPNG = toFormat("png")
  def toSVG = toFormat("svg")

  private def doc(relativeDimensions: Boolean) = {
    // Turn back into DOM
    val parserName = XMLResourceDescriptor.getXMLParserClassName()
    val f = new SAXSVGDocumentFactory(parserName)
    val doc = f.createDocument("file:///test.svg",
      new CharArrayReader(svg.toCharArray()))
    val h = doc.getDocumentElement().getAttribute("height")
    val w = doc.getDocumentElement().getAttribute("width")
    doc.getDocumentElement().setAttributeNS(null, "viewbox", s"0 0 $w $h")
    if (relativeDimensions) {
      doc.getDocumentElement().setAttribute("height", "100%")
      doc.getDocumentElement().setAttribute("width", "100%")
    }
    doc
  }
}