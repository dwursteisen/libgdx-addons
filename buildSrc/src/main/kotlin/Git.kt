object Git {
    fun commit() = Runtime.getRuntime()
        .exec("git rev-parse --short HEAD")
        .inputStream.bufferedReader()
        .readText()
        .trimIndent()
}
