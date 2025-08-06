# AI Agent Workflow Guide

## What Does an AI Agent Do When Working on This Repository?

Welcome! If you're new to this project, this document explains how an AI agent approaches working on the Illinois Child Care Assistance Program (CCAP) application. This can help you understand the development workflow and best practices.

## Overview of the IL-GCC Application

This application helps Illinois families apply online for the Child Care Assistance Program (CCAP). It's built using:
- **Java Spring Boot** with Code for America's Form Flow Library
- **Gradle** for build management
- **PostgreSQL** for data storage
- **USWDS** (US Web Design System) for frontend styling
- **Selenium** for automated testing

## AI Agent Workflow Process

### 1. Initial Repository Analysis
When given a task, an AI agent first:
- **Explores the repository structure** to understand the codebase organization
- **Reads key documentation** (README.md, technical specs, API docs)
- **Identifies the technology stack** and development tools
- **Checks the current state** of the code (build status, test results, git history)
- **Identifies any existing issues** that need to be resolved before starting work

> **Note**: As of the current state, this repository has some compilation issues related to Form Flow library dependencies that would need to be resolved before normal development can proceed. An AI agent would first work to fix these foundational issues.

### 2. Understanding the Issue
Before making any changes, the agent:
- **Analyzes the problem statement** thoroughly
- **Identifies affected components** (frontend, backend, database, etc.)
- **Reviews related code areas** that might be impacted
- **Checks for existing tests** that validate the functionality

### 3. Planning the Approach
The agent creates a detailed plan that:
- **Makes minimal changes** - only touches files that need modification
- **Preserves existing functionality** - doesn't break working features
- **Follows project conventions** - uses existing patterns and styles
- **Includes testing strategy** - validates changes work correctly

### 4. Development Process

#### Code Changes
- **Makes surgical edits** - changes as few lines as possible
- **Uses existing tools** like Gradle tasks, npm scripts, and testing frameworks
- **Follows coding standards** established in the project
- **Maintains consistency** with existing code patterns

#### Testing Strategy
- **Runs existing tests** to ensure no regressions
- **Creates focused test cases** for new functionality when needed
- **Uses the project's testing tools**:
  - `./gradlew test` - Unit and integration tests
  - `./gradlew accessibilityTest` - Accessibility compliance tests
  - Selenium for UI journey tests

#### Build and Validation
- **Compiles the application** using `./gradlew build`
- **Runs linting tools** to ensure code quality
- **Tests in development environment** to verify functionality
- **Validates against requirements** to ensure the issue is resolved

### 5. Iterative Development
The agent works in small increments:
- **Makes one change at a time** and validates it works
- **Commits progress frequently** using meaningful commit messages
- **Reports progress** to stakeholders with clear status updates
- **Adjusts approach** if issues are discovered during development

## Example Workflow for Common Tasks

### Bug Fix Example
1. **Reproduce the bug** by running the application locally
2. **Identify the root cause** by examining logs and code
3. **Write a test** that fails due to the bug (if one doesn't exist)
4. **Fix the bug** with minimal code changes
5. **Verify the test passes** and no other tests break
6. **Commit and report progress**

### Feature Addition Example
1. **Review feature requirements** and acceptance criteria
2. **Design the solution** following existing architecture patterns
3. **Implement backend changes** (models, services, controllers)
4. **Update frontend components** (templates, styles, JavaScript)
5. **Add comprehensive tests** for the new functionality
6. **Update documentation** if needed
7. **Validate end-to-end functionality**

### Maintenance Task Example
1. **Update dependencies** using Gradle or npm
2. **Run full test suite** to check for compatibility issues
3. **Fix any breaking changes** introduced by updates
4. **Update configuration** if required
5. **Test in development environment**
6. **Document any new requirements or changes**

## Key Principles

### Minimal Impact
- Change only what's necessary to solve the problem
- Preserve existing working functionality
- Avoid refactoring unrelated code

### Quality Assurance
- Always run tests before and after changes
- Use the project's linting and formatting tools
- Follow established coding patterns and conventions

### Documentation
- Keep code changes well-documented
- Update relevant documentation when needed
- Write clear commit messages explaining changes

### Collaboration
- Report progress frequently
- Ask for clarification when requirements are unclear
- Follow the project's contribution guidelines

## Development Environment Setup

If you want to work on this project yourself, follow the setup instructions in the main README.md:

1. **Install dependencies**: Java 21, Gradle 8.11+, PostgreSQL 14+, Node.js
2. **Set up environment variables** using the `.env` file
3. **Create databases** for development and testing
4. **Generate HTTPS certificates** for local development
5. **Run the application** and verify it works

## Common Tools and Commands

### Building and Testing
```bash
# Build the application (note: currently has dependency issues that need resolution)
./gradlew build

# Run all tests (requires build to work first)
./gradlew clean test

# Run accessibility tests
./gradlew accessibilityTest

# Run a specific test
./gradlew test --tests org.ilgcc.app.journeys.GccFlowJourneyTest.fullGccFlow

# Check dependencies and resolve issues
./gradlew dependencies
```

### Frontend Development
```bash
# Install frontend dependencies
npm install

# Compile SCSS to CSS
./gradlew compileSass

# Compile JavaScript
./gradlew compileJs

# Watch for changes (development)
./gradlew watchCompileSass
./gradlew watchCompileJs
```

### Database Management
```bash
# Generate new migration
./scripts/generate_migration.sh

# Run database migrations
./gradlew flywayMigrate
```

## Getting Help

- **Main README.md**: Comprehensive setup and development instructions
- **Form Flow Library docs**: https://github.com/codeforamerica/form-flow
- **Issue templates**: Use GitHub issue templates for bug reports and feature requests
- **Pull request template**: Follow the PR template for code contributions

## Current Known Issues

As of the latest repository state, there are some compilation issues that need to be addressed:

### Form Flow Library Dependency Issues
The application currently has compilation errors related to missing Form Flow library classes:
- `formflow.library.data.Submission`
- `formflow.library.data.SubmissionRepositoryService`

**Potential solutions an AI agent would investigate:**
1. **Check Form Flow library version compatibility** - The current version (1.6.38) may need updating
2. **Verify dependency resolution** - Ensure Maven/Gradle can download the library
3. **Check environment configuration** - Some environment variables may be required for dependency resolution
4. **Review library API changes** - The Form Flow library may have breaking changes requiring code updates

These foundational issues would be the first priority for an AI agent to resolve before proceeding with feature development or bug fixes.

This workflow ensures consistent, high-quality contributions to the IL-GCC application while maintaining the stability and reliability that families in Illinois depend on for accessing child care assistance.