package au.edu.uq.aorra.charts

import org.apache.batik.dom.svg.SAXSVGDocumentFactory
import org.apache.batik.util.XMLResourceDescriptor
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.batik.transcoder.{ TranscoderInput, TranscoderOutput }
import org.apache.batik.transcoder.svg2svg.SVGTranscoder
import java.io.ByteArrayOutputStream
import java.io.StringWriter
import java.io.CharArrayReader
import org.apache.batik.transcoder.SVGAbstractTranscoder

object SvgWrapper {
  def apply(svg: String) = new SvgWrapper(svg)
}

class SvgWrapper(svg: String) {

  def toSVG = {
    val t = new SVGTranscoder()
    val w = new StringWriter()
    t.transcode(new TranscoderInput(document(true)), new TranscoderOutput(w))
    w.toString()
  }

  def toPNG = {
    val t = new PNGTranscoder()
    val os = new ByteArrayOutputStream()
    t.transcode(new TranscoderInput(document(false)), new TranscoderOutput(os))
    os.toByteArray()
  }

  private def document(relativeDimenions: Boolean) = {
    // Turn back into DOM
    val parserName = XMLResourceDescriptor.getXMLParserClassName()
    val f = new SAXSVGDocumentFactory(parserName)
    val doc = f.createDocument("file:///test.svg",
        new CharArrayReader(svg.toCharArray()))
    val h = doc.getDocumentElement().getAttribute("height")
    val w = doc.getDocumentElement().getAttribute("width")
    doc.getDocumentElement().setAttributeNS(null, "viewbox", s"0 0 $w $h")
    if (relativeDimenions) {
      doc.getDocumentElement().setAttribute("height", "100%")
      doc.getDocumentElement().setAttribute("width", "100%")
    }
    doc
  }
}