package com.example.playcoach.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.playcoach.R
import com.example.playcoach.data.entities.MatchdayEntity
import com.example.playcoach.data.entities.PlayerEntity
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.*
import java.io.ByteArrayOutputStream
import java.io.File

fun generateHeadlinePdf(
    context: Context,
    matchday: MatchdayEntity,
    starters: List<PlayerEntity>,
    substitutes: List<PlayerEntity>
): File {
    val pdfFile = File(context.cacheDir, "Equipo_Jornada_${matchday.matchdayNumber}.pdf")
    val document = PdfDocument(PdfWriter(pdfFile))
    val pdfDoc = Document(document)

    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo_sln)
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val imageData = ImageDataFactory.create(stream.toByteArray())
    val logo = Image(imageData).setHeight(50f).setWidth(50f)

    val title = Paragraph("EQUIPO JORNADA ${matchday.matchdayNumber}")
        .setFontSize(20f)
        .setBold()
        .setTextAlignment(TextAlignment.LEFT)
        .setMarginLeft(10f)

    val headerTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 5f)))
        .useAllAvailableWidth()
        .addCell(Cell().add(logo).setBorder(Border.NO_BORDER))
        .addCell(Cell().add(title).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER))

    pdfDoc.add(headerTable)
    pdfDoc.add(LineSeparator(SolidLine(1f)))

    val isHome = matchday.homeTeam.trim().equals(matchday.team, ignoreCase = true)
    val opponent = if (isHome) matchday.awayTeam else matchday.homeTeam
    val condition = if (isHome) "🏟️ Local" else "🛫 Visitante"

    pdfDoc.add(Paragraph("📅 Fecha: ${matchday.date}").setFontSize(12f))
    pdfDoc.add(Paragraph("🕒 Hora: ${matchday.time}").setFontSize(12f))
    pdfDoc.add(Paragraph("⚔️ Rival: $opponent").setFontSize(12f))
    pdfDoc.add(Paragraph("📍 Condición: $condition").setFontSize(12f))

    pdfDoc.add(LineSeparator(SolidLine(1f)))
    pdfDoc.add(Paragraph("\n"))

    pdfDoc.add(
        Paragraph("✅ TITULARES (${starters.size})")
            .setFontSize(16f)
            .setBold()
            .setUnderline()
    )
    starters.forEach { player ->
        pdfDoc.add(
            Paragraph("• ${player.number} - ${player.firstName}")
                .setFontSize(12f)
        )
    }

    pdfDoc.add(Paragraph("\n"))

    pdfDoc.add(
        Paragraph("🪑 SUPLENTES (${substitutes.size})")
            .setFontSize(16f)
            .setBold()
            .setUnderline()
    )
    substitutes.forEach { player ->
        pdfDoc.add(
            Paragraph("• ${player.number} - ${player.firstName}")
                .setFontSize(12f)
        )
    }

    pdfDoc.add(Paragraph("\n\n📤 Generado con PlayCoach"))
    pdfDoc.close()
    return pdfFile
}
