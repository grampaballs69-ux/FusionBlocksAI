# Build the APK with GitHub Actions

This repository includes `.github/workflows/build-apk.yml`. It builds a **debug APK** on GitHub-hosted runners; no Android SDK or ADB installation is needed on your Windows PC.

## Upload this project to GitHub

1. Sign in at https://github.com and create a new repository (for example, `FusionBlocksAI`).
2. Choose **Public** or **Private**. Either works. Do not initialize it with a README, license, or `.gitignore` because this project already has files.
3. Download and extract `FusionBlocksAI-GitHub-Build.zip` on your PC.
4. Open the repository page on GitHub, choose **Add file → Upload files**.
5. Open the extracted `FusionBlocksAI` folder and upload its *contents* (including the hidden `.github` folder). If GitHub's browser uploader hides `.github`, use GitHub Desktop or the Git commands below.
6. Commit the upload to `main`.

## Run the build

1. In the repository, open the **Actions** tab.
2. Select **Build Fusion Blocks AI APK** on the left.
3. Click **Run workflow**, select `main`, then click the green **Run workflow** button.
4. Wait for the workflow to finish with a green check mark.
5. Open that completed workflow run, scroll to **Artifacts**, and download `FusionBlocksAI-debug-apk`.
6. Extract the downloaded artifact ZIP. It contains `app-debug.apk`.
7. Transfer `app-debug.apk` to your Android phone and install it. Android may ask you to allow installation from the browser or file manager you used.

## Optional: upload with GitHub Desktop

1. Install GitHub Desktop: https://desktop.github.com/
2. Clone your empty repository.
3. Copy everything inside the extracted `FusionBlocksAI` folder into the cloned repository, including `.github`.
4. In GitHub Desktop, commit to `main` and click **Push origin**.
5. Open the repository's **Actions** tab and download the APK artifact after the build completes.

## Important status

This workflow compiles the current Android foundation project. The current source is **not yet a complete automatic Fusion Blocks player**: visual recognition of the exact game board/three pieces, coordinate calibration, and the complete screenshot → solve → drag → verify loop still need to be implemented and tested against a screenshot of your game. The APK is a debug build, not a signed Play Store release.
