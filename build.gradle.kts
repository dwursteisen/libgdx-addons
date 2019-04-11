import internal.ConfigurationPlugin

allprojects {

    apply { plugin(ConfigurationPlugin::class) }

    group = "com.github.dwursteisen.libgdx-addons"
    version = "2.0"
}
