# Sake Hack KMP - iOS Development Commands

# Fixed device: iPhone 17 Pro
DEVICE_ID = 60958EAE-9046-4FDC-8F1E-D9059E25486A
BUNDLE_ID = org.sakehack.SakehackkmpES9J8E6HFH
SCHEME = iosApp
PROJECT = iosApp.xcodeproj
APP_NAME = Sakehackkmp

.PHONY: help xcodegen build install run build-and-run clean

help:
	@echo "iOS Development Commands:"
	@echo "  make xcodegen       - Generate Xcode project from project.yml"
	@echo "  make build          - Build the iOS app"
	@echo "  make install        - Install the app to simulator"
	@echo "  make run            - Launch the app on simulator"
	@echo "  make build-and-run  - Build, install and run (all-in-one)"
	@echo "  make clean          - Clean build artifacts"

xcodegen:
	@echo "Generating Xcode project..."
	xcodegen -s project.yml

build:
	@echo "Building iOS app for $(DEVICE_ID)..."
	xcodebuild -project $(PROJECT) \
	  -scheme $(SCHEME) \
	  -destination 'platform=iOS Simulator,id=$(DEVICE_ID)' \
	  build

install: build
	@echo "Installing app to simulator..."
	xcrun simctl install $(DEVICE_ID) \
	  ~/Library/Developer/Xcode/DerivedData/iosApp-*/Build/Products/Debug-iphonesimulator/$(APP_NAME).app

run:
	@echo "Launching app..."
	xcrun simctl launch $(DEVICE_ID) $(BUNDLE_ID)

build-and-run:
	@echo "Building, installing and running iOS app..."
	xcodebuild -project $(PROJECT) \
	  -scheme $(SCHEME) \
	  -destination 'platform=iOS Simulator,id=$(DEVICE_ID)' \
	  build && \
	xcrun simctl install $(DEVICE_ID) \
	  ~/Library/Developer/Xcode/DerivedData/iosApp-*/Build/Products/Debug-iphonesimulator/$(APP_NAME).app && \
	xcrun simctl launch $(DEVICE_ID) $(BUNDLE_ID)

clean:
	@echo "Cleaning build artifacts..."
	xcodebuild -project $(PROJECT) \
	  -scheme $(SCHEME) \
	  clean
