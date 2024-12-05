package com.example.festapp_ofc

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.festapp_ofc.databinding.ActivityPixPaymentBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class PixPaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPixPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPixPaymentBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        // Ajustar padding para comportar o conteúdo da tela
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Exemplo de chave Pix (estática, pode ser alterada para gerar dinamicamente)
        val pixKey = "00020101021126690014BR.GOV.BCB.PIX0136chavepix@example.com5204000053039865404100.005802BR5908Seu Nome6009Cidade62290525d5c06bfdb16d44d48000"

        // Gerar e exibir QR Code
        generateQRCode(pixKey)?.let { qrBitmap ->
            binding.imageViewQRCode.setImageBitmap(qrBitmap)
        } ?: Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show()

        // Adiciona funcionalidade para copiar chave Pix
        binding.buttonCopyPixKey.setOnClickListener {
            copiarChavePix(pixKey)
        }
    }

    /**
     * Gera um Bitmap do QR Code a partir de uma string.
     */
    private fun generateQRCode(data: String): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Copia a chave Pix para a área de transferência.
     */
    private fun copiarChavePix(pixKey: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Chave Pix", pixKey)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Chave Pix copiada para a área de transferência!", Toast.LENGTH_SHORT).show()
    }
}
