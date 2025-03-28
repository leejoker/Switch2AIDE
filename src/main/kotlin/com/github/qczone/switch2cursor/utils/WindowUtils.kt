package com.github.qczone.switch2cursor.utils

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.SystemInfo

object WindowUtils {

    private val logger = Logger.getInstance(WindowUtils::class.java)

    fun activeWindow(cursorPath: String) {
        if (!SystemInfo.isWindows) {
            return
        }
        val processName: String
        val containTrae = cursorPath.contains("Trae")
        if (containTrae) {
            processName = "Trae CN"
        } else {
            processName = "Cursor"
        }
        try {
            val command =
                """Get-Process | Where-Object { ${'$'}_.ProcessName -eq '"$processName"' -and ${'$'}_.MainWindowTitle -match '"$processName"' } | Sort-Object { ${'$'}_.StartTime } -Descending | Select-Object -First 1 | ForEach-Object { (New-Object -ComObject WScript.Shell).AppActivate(${'$'}_.Id) }"""
            logger.info("Executing PowerShell command: $command")

            val processBuilder = ProcessBuilder("powershell", "-command", command)
            processBuilder.redirectErrorStream(true)

            val process = processBuilder.start()
            val output = process.inputStream.bufferedReader().use { it.readText() }
            logger.info("Command output: $output")

            val exitCode = process.waitFor()
            logger.info("Command completed with exit code: $exitCode")

            if (exitCode != 0) {
                logger.error("Command failed with exit code: $exitCode")
            }
        } catch (e: Exception) {
            logger.error("Failed to activate Cursor window", e)
        }
    }
}