
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import com.capevi.app.ui.case_composables.getColorForStatusContainer
import com.capevi.data.model.AuditTrail
import com.capevi.data.model.CaseModel
import com.capevi.shared.utils.formatDateText
import com.capevi.shared.utils.getFileNameWithoutExtension
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Element
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun generatePdf(
    context: Context,
    case: CaseModel,
    auditTrailList: List<AuditTrail>,
) {
    val document = Document()
    val filePath =
        File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "evidential_capture_${System.currentTimeMillis()}.pdf",
        )
    try {
        PdfWriter.getInstance(document, FileOutputStream(filePath))
        document.open()
        // Define Fonts
        val titleFont = com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 24f, com.itextpdf.text.Font.BOLD)
        val descriptionFont = com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18f)
        val statusFont = com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18f, com.itextpdf.text.Font.BOLD)
        // Title
        document.add(Paragraph(case.title, titleFont))
        // Description
        val descriptionParagraph = Paragraph(case.description, descriptionFont)
        descriptionParagraph.spacingBefore = 20f
        descriptionParagraph.spacingAfter = 20f
        document.add(descriptionParagraph)
        // Status with background color
        val statusTable = PdfPTable(1)
        statusTable.widthPercentage = 100f
        val statusCell = PdfPCell(Paragraph(case.status, statusFont))
        statusCell.backgroundColor = BaseColor(getColorForStatusContainer(case.status!!).toArgb())
        statusCell.border = PdfPCell.NO_BORDER
        statusCell.paddingTop = 10f
        statusCell.paddingBottom = 10f
        statusCell.paddingLeft = 10f
        statusCell.paddingRight = 10f
        statusCell.horizontalAlignment = Element.ALIGN_LEFT
        statusTable.addCell(statusCell)
        document.add(statusTable)
        // Time and Date of Log
        val timeParagraph = Paragraph("Time of Log: ${case.loggedAt.format(DateTimeFormatter.ofPattern("hh:mma"))}", descriptionFont)
        timeParagraph.spacingBefore = 20f
        timeParagraph.spacingAfter = 20f
        document.add(timeParagraph)
        document.add(Paragraph("Date of Log: ${formatDateText(case.loggedAt)}", descriptionFont))

        val auditParagraph = Paragraph("Audit Trail", titleFont)
        document.add(auditParagraph)
        auditParagraph.spacingAfter = 20f

        val table = PdfPTable(2)
        table.widthPercentage = 100f
        table.addCell(PdfPCell(Phrase("Timestamp", descriptionFont)))
        table.addCell(PdfPCell(Phrase("Action and Description", descriptionFont)))

        for (auditTrail in auditTrailList) {
            table.addCell(
                PdfPCell(
                    Phrase(
                        formatTimestampToReadableTime(auditTrail.timestamp),
                        descriptionFont,
                    ),
                ),
            )
            table.addCell(PdfPCell(Phrase("${auditTrail.action} - ${auditTrail.description}", descriptionFont)))
        }

        document.add(table)

        // Add Images
        val imageUrls = case.evidenceList.filter { it.contains(".jpg", ignoreCase = true) }
        val imageMargin = 0f // Margin between images (set to 0 for full width)
        val pageWidth = document.pageSize.width
        for (url in imageUrls) {
            val imageFile = File(context.cacheDir, getFileNameWithoutExtension(url))
            if (!imageFile.exists()) {
                Log.e("PDFGeneration", "Image file does not exist: ${imageFile.absolutePath}")
                continue // Skip this image or handle the error
            }
            try {
                val pdfImage = Image.getInstance(imageFile.absolutePath)
                // Scale image to fit the page width while maintaining aspect ratio
                pdfImage.scaleToFit(pageWidth, pdfImage.scaledHeight * (pageWidth / pdfImage.scaledWidth))
                pdfImage.spacingBefore = imageMargin
                pdfImage.spacingAfter = imageMargin
                document.add(pdfImage)
            } catch (e: IOException) {
                Log.e("PDFGeneration", "Error loading image: ${imageFile.absolutePath}", e)
            }
        }
        document.close()
        openPdf(context, filePath)
    } catch (e: DocumentException) {
        e.printStackTrace()
        Toast.makeText(context, "Error creating PDF: ${e.message}", Toast.LENGTH_LONG).show()
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Error creating PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

private fun openPdf(
    context: Context,
    file: File,
) {
    val uri: Uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)

    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, "application/pdf")
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No application found to open PDF", Toast.LENGTH_SHORT).show()
    }
}

fun formatTimestampToReadableTime(timestamp: Long): String {
    // Convert timestamp to Instant
    val instant = Instant.ofEpochMilli(timestamp)

    // Define the formatter
    val formatter =
        DateTimeFormatter
            .ofPattern("dd MMM yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault())

    // Format the Instant to a readable time string
    return formatter.format(instant)
}
