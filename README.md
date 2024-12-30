# Easy Translate Plugin

[中文文档](README_CN.md)

A user-friendly translation plugin for IntelliJ IDEA that supports multiple translation engines and custom dictionaries.

## Features

- Multiple Translation Engines:
  - Baidu Translate
  - Youdao Translate
  - Google Translate

- Quick Translation:
  - Translate selected text with a shortcut (default Alt + T)
  - Auto-detect Chinese/English and translate accordingly
  - Customizable keyboard shortcuts

- Custom Dictionary:
  - Add custom translation mappings
  - Case-sensitive options
  - Enable/disable individual dictionary entries

- Text Transformation:
  - Case conversion
  - Camel case conversion
  - Snake case conversion
  - Constant case conversion

## Requirements

- IntelliJ IDEA 2023.2 or higher
- Java 17 or higher

## Supported IDEs

- IntelliJ IDEA (Community & Ultimate)
- PyCharm (Community & Professional)
- WebStorm
- PhpStorm
- GoLand
- CLion
- DataGrip
- RubyMine
- Rider

## Installation

1. Open Settings/Preferences in your IDE
2. Select Plugins
3. Click Marketplace
4. Search for "Easy Translate"
5. Click Install

## Usage

### Basic Translation
1. Select the text you want to translate
2. Use the shortcut Alt + T (customizable) or right-click menu "Quick Translate"
3. The text will be automatically translated and replace the selection

### Configure Translation Engine
1. Open Settings/Preferences
2. Go to Tools -> Translation Assistant
3. Select a translation engine and configure API credentials:
   - Baidu Translate: Requires APP ID and Secret Key
   - Youdao Translate: Requires App Key and Secret Key
   - Google Translate: Optional proxy configuration

### Custom Dictionary
1. Open Settings/Preferences
2. Go to Tools -> Translation Assistant
3. Add, edit, or delete translation mappings in the dictionary settings
4. Configure case sensitivity and enable/disable options

### Text Transformation
1. Select the text you want to transform
2. Use the shortcut Alt + Shift + T or right-click menu "Text Transform"
3. Choose the transformation type:
   - UPPERCASE
   - lowercase
   - Title Case
   - camelCase
   - snake_case

## Development

### Requirements
- Java Development Kit (JDK) 17
- Gradle 8.4

### Build
```bash
./gradlew clean build
```

### Run Tests
```bash
./gradlew test
```

### Debug Locally
```bash
./gradlew runIde
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions via Issues and Pull Requests are welcome. Before submitting a PR, please ensure:

1. Code follows project coding standards
2. Necessary tests are added
3. Documentation is updated
4. All tests pass

## Support

If you encounter any issues or have suggestions:

1. Submit an Issue
2. Send email to dev@oofo.cc
3. Visit project homepage at https://www.oofo.cc 