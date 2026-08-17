package com.example.aihakem.repository

import com.example.aihakem.data.Statement
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RefereeRepository(apiKey: String) {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun evaluateDispute(
        topic: String,
        statementA: Statement,
        statementB: Statement
    ): String = withContext(Dispatchers.IO) {

        val prompt = """
            Sen tarafsız, son derece mantıklı, adil ve anlayışlı bir arabulucu / hakem rolündesin.
            İki arkadaş arasındaki bir tartışmayı değerlendireceksin.
            
            Tartışma Konusu: ${if (topic.isBlank()) "Belirtilmedi" else topic}
            
            1. Taraf (${statementA.speakerName}): "${statementA.content}"
            2. Taraf (${statementB.speakerName}): "${statementB.content}"
            
            GÖREVİN VE KURALLARIN:
            - KESİNLİKLE hiçbir ceza, yaptırım veya ödev VERME.
            - Kim haklıysa veya hangi taraf daha makul bir argüman sunduysa onu belirle.
            - Haklı olan tarafın NEDEN haklı olduğunu madde madde, açık ve mantıksal gerekçelerle izah et.
            - Haksız olan tarafa da neden bu sonucun çıktığını incitmeden, objektif bir dille anlat.
            - Üslubun dostane, dengeli, tarafsız ve yapıcı olmalı.
            
            Lütfen yanıtı tam olarak şu formatta ver:
            
            🏆 KAZANAN / HAKLI TARAF:
            [Haklı olan kişinin adı veya "Her iki taraf da eşit derecede haklı/haksız"]
            
            📌 TARTIŞMANIN ÖZETİ:
            [Tartışmanın 1-2 cümlelik tarafsız özeti]
            
            💡 GEREKÇELİ KARAR VE ANALİZ:
            [Neden bu karara varıldığının ayrıntılı, mantıklı açıklaması]
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)
        return@withContext response.text ?: "Karar oluşturulurken bir yanıt alınamadı."
    }
}
