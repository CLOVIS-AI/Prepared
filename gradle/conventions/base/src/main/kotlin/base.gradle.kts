package conventions

plugins {
	base
}

val appVersion: String? by project

group = "dev.opensavvy.prepared"
version = appVersion ?: "0.0.0-DEV"
