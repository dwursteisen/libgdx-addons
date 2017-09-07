package com.github.dwursteisen.libgdx

object ServiceLocator {

    private var cache: Map<Class<out Any>, Any> = emptyMap()

    operator fun <T> get(clazz: Class<T>): T {
        val result = cache[clazz] ?: TODO("Unknow service register with the class $clazz")
        return result as T
   }

   inline fun <reified T> locate(): T {
       return this.get(T::class.java)
   }

   fun register(instance: Any, clazz: Class<out Any>) {
        cache += clazz to instance
   }
}