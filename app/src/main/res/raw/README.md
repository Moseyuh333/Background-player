# IMPORTANT: Add FLAC Files

This directory should contain FLAC audio files for the demo tracks.

## Required Files:
1. `sample1.flac` - Demo Track 1
2. `sample2.flac` - Demo Track 2  
3. `sample3.flac` - Demo Track 3

## How to Add FLAC Files:

Since FLAC files are binary and large, you need to manually add them to this directory.

### Option 1: Use Existing FLAC Files
Copy your FLAC audio files to this directory and rename them as:
- `sample1.flac`
- `sample2.flac`
- `sample3.flac`

### Option 2: Convert Audio to FLAC
You can convert MP3 or other audio formats to FLAC using tools like:
- **FFmpeg**: `ffmpeg -i input.mp3 output.flac`
- **Audacity**: Free audio editor with FLAC export
- **Online converters**: Search "convert to FLAC online"

### Option 3: Use the Provided FLAC File
I notice there's a file `1_5131934797106512485.flac` in the root directory.
You can copy this file three times and rename as needed:
1. Copy to `app/src/main/res/raw/sample1.flac`
2. Copy to `app/src/main/res/raw/sample2.flac`
3. Copy to `app/src/main/res/raw/sample3.flac`

### Using Windows Command Prompt:
```cmd
mkdir app\src\main\res\raw
copy 1_5131934797106512485.flac app\src\main\res\raw\sample1.flac
copy 1_5131934797106512485.flac app\src\main\res\raw\sample2.flac
copy 1_5131934797106512485.flac app\src\main\res\raw\sample3.flac
```

## Important Notes:
- Raw resource files must be lowercase with no special characters
- FLAC files can be large, so keep them reasonably small for demo purposes
- Android Studio will automatically include these files in the APK
- For production apps, consider loading from external storage or streaming

## Testing Without FLAC Files:
If you don't have FLAC files immediately available, you can:
1. Temporarily modify the app to use MP3 files (remove the FLAC-specific ExoPlayer dependency)
2. Or download free FLAC samples from: https://www.2l.no/hires/ or https://archive.org/

