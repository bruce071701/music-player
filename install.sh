#!/bin/zsh
# ============================================
# Music Player - Install Script
# ============================================
# Usage:
#   ./install.sh              - Build debug & install to connected device
#   ./install.sh release      - Build release & install
#   ./install.sh --no-build   - Install existing APK without rebuilding
#   ./install.sh --device ID  - Install to specific device
# ============================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo "${GREEN}🎵 Music Player Install Script${NC}"
echo "================================"

# Parse arguments
BUILD_TYPE="debug"
NO_BUILD=false
DEVICE_ID=""

while [[ $# -gt 0 ]]; do
    case "$1" in
        release)
            BUILD_TYPE="release"
            shift
            ;;
        --no-build)
            NO_BUILD=true
            shift
            ;;
        --device)
            DEVICE_ID="$2"
            shift 2
            ;;
        *)
            echo "${RED}Unknown argument: $1${NC}"
            echo "Usage: ./install.sh [release] [--no-build] [--device DEVICE_ID]"
            exit 1
            ;;
    esac
done

# Check ANDROID_HOME & adb
if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
    fi
fi

ADB="$ANDROID_HOME/platform-tools/adb"
if [ ! -f "$ADB" ]; then
    ADB=$(which adb 2>/dev/null || true)
    if [ -z "$ADB" ]; then
        echo "${RED}❌ adb not found. Ensure Android SDK platform-tools is installed.${NC}"
        exit 1
    fi
fi

# Check connected devices
echo "\n📱 Checking connected devices..."
DEVICES=$("$ADB" devices | grep -v "List" | grep "device$" | awk '{print $1}')

if [ -z "$DEVICES" ]; then
    echo "${RED}❌ No devices connected.${NC}"
    echo "   Please connect a device via USB or start an emulator."
    echo "   Tips:"
    echo "   - USB: Enable Developer Options > USB Debugging"
    echo "   - Emulator: $ANDROID_HOME/emulator/emulator -list-avds"
    exit 1
fi

DEVICE_COUNT=$(echo "$DEVICES" | wc -l | tr -d ' ')
echo "${GREEN}✅ Found $DEVICE_COUNT device(s):${NC}"
echo "$DEVICES" | while read -r device; do
    MODEL=$("$ADB" -s "$device" shell getprop ro.product.model 2>/dev/null || echo "Unknown")
    VERSION=$("$ADB" -s "$device" shell getprop ro.build.version.release 2>/dev/null || echo "?")
    echo "   📱 $device ($MODEL, Android $VERSION)"
done

# Select device
if [ -n "$DEVICE_ID" ]; then
    TARGET_DEVICE="$DEVICE_ID"
elif [ "$DEVICE_COUNT" -gt 1 ]; then
    echo "\n${YELLOW}⚠️  Multiple devices found. Use --device ID to specify.${NC}"
    echo "   Using first device..."
    TARGET_DEVICE=$(echo "$DEVICES" | head -1)
else
    TARGET_DEVICE=$(echo "$DEVICES" | head -1)
fi

echo "\n🎯 Target: $TARGET_DEVICE"

# Build if needed
if [ "$NO_BUILD" = false ]; then
    echo "\n🔨 Building $BUILD_TYPE APK..."
    ./build.sh "$BUILD_TYPE"
fi

# Find APK - auto detect device ABI
DEVICE_ABI=$("$ADB" -s "$TARGET_DEVICE" shell getprop ro.product.cpu.abi 2>/dev/null || echo "arm64-v8a")
echo "🏗️  Device ABI: $DEVICE_ABI"

if [ "$BUILD_TYPE" = "release" ]; then
    APK_PATH=$(find app/build/outputs/apk/release -name "*${DEVICE_ABI}*.apk" 2>/dev/null | head -1)
    if [ -z "$APK_PATH" ]; then
        APK_PATH=$(find app/build/outputs/apk/release -name "*.apk" | head -1)
    fi
else
    APK_PATH=$(find app/build/outputs/apk/debug -name "*${DEVICE_ABI}*.apk" 2>/dev/null | head -1)
    if [ -z "$APK_PATH" ]; then
        APK_PATH=$(find app/build/outputs/apk/debug -name "*.apk" | head -1)
    fi
fi

if [ ! -f "$APK_PATH" ]; then
    echo "${RED}❌ APK not found at: $APK_PATH${NC}"
    echo "   Run ./build.sh first."
    exit 1
fi

APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
echo "\n📦 Installing: $APK_PATH ($APK_SIZE)"

# Uninstall old version (ignore if not installed)
echo "🗑️  Removing old version (if exists)..."
"$ADB" -s "$TARGET_DEVICE" uninstall com.app.musicplayer 2>/dev/null || true

# Install
echo "📲 Installing..."
"$ADB" -s "$TARGET_DEVICE" install -r "$APK_PATH"

if [ $? -eq 0 ]; then
    echo "${GREEN}✅ Installation successful!${NC}"
else
    echo "${RED}❌ Installation failed${NC}"
    exit 1
fi

# Launch the app
echo "\n🚀 Launching Music Player..."
"$ADB" -s "$TARGET_DEVICE" shell am start -n com.app.musicplayer/.MainActivity

echo "\n================================"
echo "${GREEN}🎵 Music Player is running on your device!${NC}"
echo ""
echo "${CYAN}Tips:${NC}"
echo "  - Grant storage permission when prompted"
echo "  - The app will auto-scan your music library"
echo "  - Use logcat to debug: adb logcat -s MusicPlayer"
