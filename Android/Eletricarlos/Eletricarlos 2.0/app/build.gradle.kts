plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.eletricarlos"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.eletricarlos"
        minSdk = 24
        targetSdk = 36
        versionCode = 2  // Incrementado para ser reconhecido como atualização
        versionName = "2.0"  // Versão 2.0 do Eletricarlos

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // PASSO 1: Configurar assinatura digital (necessário para Release)
    // Para criar a keystore, execute no terminal:
    // keytool -genkey -v -keystore eletricarlos.jks -keyalg RSA -keysize 2048 -validity 10000 -alias eletricarlos
    // Guarde a keystore e as senhas em local seguro!
    
    signingConfigs {
        create("release") {
            // Descomente e configure após criar a keystore:
            // storeFile = file("eletricarlos.jks")  // Caminho para sua keystore
            // storePassword = "SuaSenhaAqui"        // Senha da keystore
            // keyAlias = "eletricarlos"              // Alias da chave
            // keyPassword = "SuaSenhaAqui"          // Senha da chave
        }
    }
    
    buildTypes {
        release {
            // Ativar minificação e ofuscação para segurança
            isMinifyEnabled = true
            isShrinkResources = true
            
            // Ativar ProGuard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Configurações de segurança
            isDebuggable = false
            isJniDebuggable = false
            
            // IMPORTANTE: Descomente após configurar signingConfigs acima
            // signingConfig = signingConfigs.getByName("release")
        }
        
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    
    // Renomear APKs automaticamente com versão
    applicationVariants.all {
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val versionName = defaultConfig.versionName
            val buildType = buildType.name
            
            // Gera nomes tipo: Eletricarlos-v2.0-release.apk
            output.outputFileName = "Eletricarlos-v${versionName}-${buildType}.apk"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Configurações de segurança adicionais
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    
    // Gson for JSON parsing (needed for legacy data migration)
    implementation("com.google.code.gson:gson:2.10.1")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}