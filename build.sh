#!/bin/zsh
# ============================================
# Music Player - Build Script
# ============================================
# Usage:
#   ./build.sh          - Debug build
#   ./build.sh release  - Release build (signed)
#   ./build.sh clean    - Clean build
#   ./build.sh bundle   - AAB for Play Store
# ============================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "${GREEN}🎵 Music Player Build Script${NC}"
echo "================================"

# Check ANDROID_HOME
if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
    else
        echo "${RED}❌ ANDROID_HOME not set. Please set it to your Android SDK path.${NC}"
        echo "   export ANDROID_HOME=\$HOME/Library/Android/sdk"
        exit 1
    fi
fi
echo "📱 ANDROID_HOME: $ANDROID_HOME"

# Check Java
if ! command -v java &> /dev/null; then
    echo "${RED}❌ Java not found. Please install JDK 17+.${NC}"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
echo "☕ Java version: $JAVA_VERSION"

# Check local.properties
if [ ! -f "local.properties" ]; then
    echo "${YELLOW}⚠️  local.properties not found, creating...${NC}"
    echo "sdk.dir=$ANDROID_HOME" > local.properties
    echo "YOUTUBE_API_KEY=YOUR_KEY_HERE" >> local.properties
    echo "LASTFM_API_KEY=" >> local.properties
    echo "LASTFM_API_SECRET=" >> local.properties
fi

# Ensure sdk.dir is in local.properties
if ! grep -q "sdk.dir" local.properties; then
    echo "sdk.dir=$ANDROID_HOME" >> local.properties
fi

# Make gradlew executable
if [ ! -f "gradlew" ]; then
    echo "${YELLOW}⚠️  Gradle wrapper not found, generating...${NC}"
    gradle wrapper --gradle-version 8.6
fi
chmod +x gradlew

# Build based on argument
BUILD_TYPE="${1:-debug}"

case "$BUILD_TYPE" in
    "clean")
        echo "\n🧹 Cleaning project..."
        ./gradlew clean
        echo "${GREEN}✅ Clean complete${NC}"
        ;;
    "debug")
        echo "\n🔨 Building DEBUG APK..."
        ./gradlew assembleDebug --stacktrace
        APK_PATH=$(find app/build/outputs/apk/debug -name "*.apk" | head -1)
        if [ -n "$APK_PATH" ] && [ -f "$APK_PATH" ]; then
            APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
            echo "${GREEN}✅ Debug build successful${NC}"
            echo "📦 APKs:"
            find app/build/outputs/apk/debug -name "*.apk" -exec sh -c 'echo "   $(du -h "$1" | cut -f1) - $1"' _ {} \;
        else
            echo "${RED}❌ Build failed - APK not found${NC}"
            exit 1
        fi
        ;;
    "release")
        echo "\n🔨 Building RELEASE APK..."
        ./gradlew assembleRelease --stacktrace
        echo "${GREEN}✅ Release build successful${NC}"
        echo "📦 APKs (per ABI):"
        find app/build/outputs/apk/release -name "*.apk" -exec sh -c 'echo "   $(du -h "$1" | cut -f1) - $1"' _ {} \;
        ;;
    "bundle")
        echo "\n🔨 Building AAB (App Bundle) for Play Store..."
        ./gradlew bundleRelease --stacktrace
        AAB_PATH="app/build/outputs/bundle/release/app-release.aab"
        if [ -f "$AAB_PATH" ]; then
            AAB_SIZE=$(du -h "$AAB_PATH" | cut -f1)
            echo "${GREEN}✅ Bundle build successful${NC}"
            echo "📦 AAB: $AAB_PATH ($AAB_SIZE)"
        else
            echo "${RED}❌ Bundle build failed${NC}"
            exit 1
        fi
        ;;
    *)
        echo "${RED}Unknown build type: $BUILD_TYPE${NC}"
        echo "Usage: ./build.sh [debug|release|clean|bundle]"
        exit 1
        ;;
esac

echo "\n================================"
echo "${GREEN}🎵 Done!${NC}"
