# Nova Assistant

Msaidizi wako wa AI kwenye simu yako ya Android.

## Vipengele

- **Voice Control**: Amrisha kwa sauti
- **WhatsApp Auto-Send**: Tuma ujumbe wa WhatsApp moja kwa moja
- **Phone Control**: Piga simu, tuma SMS, fungua app
- **Background Service**: Fanya kazi background
- **Accessibility**: Udhibiti wa simu yote

## Jinsi ya Kutumia

1. **Build App**
   ```bash
   cd NovaAssistant
   ./gradlew assembleDebug
   ```

2. **Weka kwenye Simu**
   - Weka APK kwenye simu yako
   - Samvunja "Unknown Sources" kwenye mipangilio
   - Weka app

3. **Ruhusa**
   - Ruhusa za sauti
   - Ruhusa za simu
   - Ruhusa za WhatsApp
   - Accessibility Service

4. **Anza Kutumia**
   - Fungua app
   - Bonyeza kitufe cha mic
   - Sema amri yako

## Amri za Sauti

- "Habari Nova" - Anza mazungumzo
- "Tuma ujumbe kwa [jina]" - Tuma WhatsApp
- "Piga simu [jina]" - Piga simu
- "Fungua [app]" - Fungua programu

##的技术 Stack

- Kotlin
- Android SpeechRecognizer
- Android TextToSpeech
- Accessibility Service
- Foreground Service

## Mahitaji

- Android 7.0 (API 24) na zaidi
- Ruhusa za sauti
- Ruhusa za simu
- WhatsApp imefungwa
