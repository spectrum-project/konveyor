package codes.spectrum.konveyor.exceptions

class NotFoundEnvElementException(field: String) : RuntimeException("Element $field is not found in environment")
