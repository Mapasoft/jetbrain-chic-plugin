#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

ACTION="${1:-build}"

case "$ACTION" in
    build)
        echo "Building chic-clion-plugin..."
        ./gradlew buildPlugin
        echo ""
        echo "Plugin zip: build/distributions/"
        ls build/distributions/*.zip 2>/dev/null || echo "(no zip found — check build output)"
        ;;
    clean)
        echo "Cleaning..."
        ./gradlew clean
        ;;
    run)
        echo "Launching CLion sandbox with plugin..."
        ./gradlew runIde
        ;;
    lexer)
        echo "Generating lexer..."
        ./gradlew generateChicLexer
        ;;
    *)
        echo "Usage: $0 {build|clean|run|lexer}"
        echo ""
        echo "  build  - Build the plugin zip (default)"
        echo "  clean  - Clean build artifacts"
        echo "  run    - Launch CLion sandbox with the plugin installed"
        echo "  lexer  - Regenerate the JFlex lexer only"
        exit 1
        ;;
esac
