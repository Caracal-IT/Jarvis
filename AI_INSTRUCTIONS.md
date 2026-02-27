# AI Coding Instructions & Guidelines

This document outlines the standards and rules for all AI-generated code and assets in the Jarvis project.

## 1. Best Practices
- **Adhere to standard conventions**: Follow official Kotlin and Android coding conventions (e.g., naming, indentation, file structure).
- **Clean Architecture**: Ensure clear separation of concerns (UI, Domain, Data).
- **Security**: Avoid hardcoding secrets. Validate inputs. Use secure dependencies.

## 2. Resource Management
- **No Hardcoded Values**: All strings, colors, dimensions, and other constants must be extracted to resource files (`strings.xml`, `colors.xml`, `dimens.xml`, etc.).
- **Centralized Assets**: Place all drawables and mipmaps in their appropriate resource directories.

## 3. Design Patterns
- **Use Standard Patterns**: Apply design patterns (e.g., Singleton, Factory, Observer, MVVM) where they solve a specific problem effectively.
- **Avoid Over-engineering**: Use patterns to simplify or decouple code, not to add unnecessary complexity.

## 4. Maintainability
- **Readable Code**: Write self-documenting code with meaningful variable and function names.
- **Documentation**: Add KDoc comments for complex logic or public APIs.
- **Modularity**: Break down large functions and classes into smaller, reusable components.
- **Testability**: Write code that is easy to unit test.

## 5. Asset Coherence
- **Consistent Aesthetic**: All generated assets (icons, backgrounds, UI elements) must adhere to the established "Jarvis / Iron Man" theme.
- **Color Palette**: Strictly stick to the defined color palette (Iron Man Red `#7A0019`, Gold `#F1D56D`, Cyan `#00E5FF`, Dark Tech `#020810`).
- **Style**: Maintain a futuristic, high-tech, geometric vector style for visual elements.

