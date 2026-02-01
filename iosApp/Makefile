# Sake Hack KMP - iOS Development Commands

# Simulator device: iPhone 17 Pro
SIMULATOR_DEVICE_ID = 60958EAE-9046-4FDC-8F1E-D9059E25486A
# Physical device: iPhone 15 Pro Max
DEVICE_ID = 00008130-0006453E1E43001C
BUNDLE_ID = org.sakehack.SakehackkmpES9J8E6HFH
SCHEME = iosApp
PROJECT = iosApp.xcodeproj
APP_NAME = Sakehackkmp

.PHONY: help xcodegen build install run build-and-run clean device-build device-install device-run device-build-and-run

help:
	@echo "iOS Development Commands:"
	@echo "  make xcodegen           - Generate Xcode project from project.yml"
	@echo "  make build              - Build the iOS app for simulator"
	@echo "  make install            - Install the app to simulator"
	@echo "  make run                - Launch the app on simulator"
	@echo "  make build-and-run      - Build, install and run on simulator (all-in-one)"
	@echo "  make device-build       - Build for physical device"
	@echo "  make device-install     - Install to physical device"
	@echo "  make device-run         - Launch on physical device"
	@echo "  make device-build-and-run - Build, install and run on device (all-in-one)"
	@echo "  make clean              - Clean build artifacts"

xcodegen:
	@echo "Generating Xcode project..."
	xcodegen -s project.yml

build:
	@echo "Building iOS app for simulator..."
	xcodebuild -project $(PROJECT) \
	  -scheme $(SCHEME) \
	  -destination 'platform=iOS Simulator,id=$(SIMULATOR_DEVICE_ID)' \
	  build

install: build
	@echo "Installing app to simulator..."
	xcrun simctl install $(SIMULATOR_DEVICE_ID) \
	  ~/Library/Developer/Xcode/DerivedData/iosApp-*/Build/Products/Debug-iphonesimulator/$(APP_NAME).app

run:
	@echo "Launching app on simulator..."
	xcrun simctl launch $(SIMULATOR_DEVICE_ID) $(BUNDLE_ID)

build-and-run:
	@echo "Building, installing and running iOS app on simulator..."
	xcodebuild -project $(PROJECT) \
	  -scheme $(SCHEME) \
	  -destination 'platform=iOS Simulator,id=$(SIMULATOR_DEVICE_ID)' \
	  build && \
	xcrun simctl install $(SIMULATOR_DEVICE_ID) \
	  ~/Library/Developer/Xcode/DerivedData/iosApp-*/Build/Products/Debug-iphonesimulator/$(APP_NAME).app && \
	xcrun simctl launch $(SIMULATOR_DEVICE_ID) $(BUNDLE_ID)

device-build:
	@echo "Building iOS app for physical device..."
	xcodebuild -project $(PROJECT) \
	  -scheme $(SCHEME) \
	  -destination 'platform=iOS,id=$(DEVICE_ID)' \
	  -configuration Debug \
	  build

device-install: device-build
	@echo "Installing app to physical device..."
	xcrun devicectl device install app --device $(DEVICE_ID) \
	  ~/Library/Developer/Xcode/DerivedData/iosApp-*/Build/Products/Debug-iphoneos/$(APP_NAME).app

device-run:
	@echo "Launching app on physical device..."
	xcrun devicectl device process launch --device $(DEVICE_ID) $(BUNDLE_ID)

device-build-and-run:
	@echo "Building, installing and running iOS app on physical device..."
	xcodebuild -project $(PROJECT) \
	  -scheme $(SCHEME) \
	  -destination 'platform=iOS,id=$(DEVICE_ID)' \
	  -configuration Debug \
	  build && \
	xcrun devicectl device install app --device $(DEVICE_ID) \
	  ~/Library/Developer/Xcode/DerivedData/iosApp-*/Build/Products/Debug-iphoneos/$(APP_NAME).app && \
	xcrun devicectl device process launch --device $(DEVICE_ID) $(BUNDLE_ID)

clean:
	@echo "Cleaning build artifacts..."
	xcodebuild -project $(PROJECT) \
	  -scheme $(SCHEME) \
	  clean
