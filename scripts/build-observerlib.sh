#!/usr/bin/env sh
set -eu

root=$(CDPATH='' cd -- "$(dirname -- "$0")/.." && pwd)
revision=053aa0076f9e0f4948e6f99cd8195c0fd6d1ed59
workspace="$root/.gradle/observerlib"
mkdir -p "$workspace"
staging=$(mktemp -d "$workspace/source.XXXXXXXX")
trap 'rm -rf "$staging"' EXIT

curl --fail --silent --show-error --location --retry 2 \
    "https://codeload.github.com/HellFirePvP/ObserverLib/zip/$revision" \
    --output "$staging/observerlib.zip"
unzip -q "$staging/observerlib.zip" -d "$staging"
source_dir="$staging/ObserverLib-$revision"
git -C "$source_dir" apply "$root/scripts/observerlib/forge-1.20.1.patch"

"$root/gradlew" --project-dir "$source_dir" \
    "-Pobserverlib_repository=$workspace/maven" \
    publishMavenJavaPublicationToPortRepository "$@"
