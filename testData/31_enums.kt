package test

enum class MyEnum {
  VALUE1, VALUE2, VALUE3
}

enum class AnotherEnum {
  VAL1, // A comment
  VAL2, // Also a comment
  VAL3 // Another comment
}

enum class SingleLine { VALUE1, VALUE2 }

enum class ConformingEnum: Serializable {
  VAL1,
  VAL2
}
