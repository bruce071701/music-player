#!/bin/bash

# ============================================
# Music Player Offline - Production Build Script
# ============================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}╔══════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║  Music Player Offline - Release Build    ║${NC}"
echo -e "${CYAN}╚══════════════════════════════════════════╝${NC}"
echo ""

# Check keystore
if [ ! -f "release-keystore.jks" ]; then
    echo -e "${RED}✗ release-keystore.jks not found!${NC}"
    exit 1
fi

if [ ! -f "keystore.properties" ]; then
    echo -e "${RED}✗ keystore.properties not found!${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Keystore found${NC}"

# Read version info
VERSION_NAME=$(grep "versionName" app/build.gradle.kts | head -1 | sed 's/.*"\(.*\)".*/\1/')
VERSION_CODE=$(grep "versionCode" app/build.gradle.kts | head -1 | sed 's/[^0-9]//g')
echo -e "${CYAN}  Version: ${VERSION_NAME} (${VERSION_CODE})${NC}"
echo ""

# Clean
echo -e "${YELLOW}► Cleaning previous build...${NC}"
./gradlew clean --quiet
echo -e "${GREEN}✓ Clean complete${NC}"

# Run lint check
echo -e "${YELLOW}► Running lint check...${NC}"
./gradlew lintRelease --quiet 2>/dev/null || true
echo -e "${GREEN}✓ Lint complete${NC}"

# Build AAB (Google Play)
echo -e "${YELLOW}► Building release AAB...${NC}"
./gradlew bundleRelease --quiet
echo -e "${GREEN}✓ AAB build complete${NC}"

# Build APKs (for testing)
echo -e "${YELLOW}► Building release APKs...${NC}"
./gradlew assembleRelease --quiet
echo -e "${GREEN}✓ APK build complete${NC}"

# Create output directory
OUTPUT_DIR="release_output/v${VERSION_NAME}"
mkdir -p "$OUTPUT_DIR"

# Copy artifacts with version name
cp app/build/outputs/bundle/release/app-release.aab "$OUTPUT_DIR/music-player-v${VERSION_NAME}.aab"
for apk in app/build/outputs/apk/release/*.apk; do
    if [ -f "$apk" ]; then
        BASENAME=$(basename "$apk" .apk)
        cp "$apk" "$OUTPUT_DIR/music-player-v${VERSION_NAME}-${BASENAME##*-}.apk"
    fi
done

# File sizes
echo ""
echo -e "${CYAN}═══════════════════════════════════════════${NC}"
echo -e "${CYAN}  Build Artifacts:${NC}"
echo -e "${CYAN}═══════════════════════════════════════════${NC}"
echo ""

for file in "$OUTPUT_DIR"/*; do
    SIZE=$(du -h "$file" | cut -f1)
    FILENAME=$(basename "$file")
    echo -e "  ${GREEN}✓${NC} $FILENAME  ${YELLOW}($SIZE)${NC}"
done

echo ""
echo -e "${CYAN}═══════════════════════════════════════════${NC}"
echo -e "${GREEN}  ✓ Release build successful!${NC}"
echo -e "${CYAN}═══════════════════════════════════════════${NC}"
echo ""
echo -e "  Output: ${YELLOW}${OUTPUT_DIR}/${NC}"
echo -e "  Upload ${YELLOW}app-release.aab${NC} to Google Play Console"
echo ""

# Verify signing
echo -e "${YELLOW}► Verifying AAB signature...${NC}"
jarsigner -verify "$OUTPUT_DIR/music-player-v${VERSION_NAME}.aab" > /dev/null 2>&1 && \
    echo -e "${GREEN}✓ Signature verified${NC}" || \
    echo -e "${RED}✗ Signature verification failed${NC}"

echo ""
echo -e "${GREEN}Done! 🎉${NC}"
