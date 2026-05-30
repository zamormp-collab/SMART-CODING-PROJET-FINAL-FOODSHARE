rootProject.name = "FoodShare"

// Include only the Android app module. The backend in `api/` is a separate Maven project
// (not a Gradle subproject) and should remain independent.
include(":app")

// If in the future the backend becomes a Gradle subproject, add it here (for now keep it separate).

