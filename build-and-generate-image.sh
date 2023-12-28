
current_version=$(awk -F\" '/version =/ {print $2}' build.gradle.kts)

echo 'Generating new version'
new_version=$(echo "$current_version" | awk -F. '{$NF+=1; OFS="."; print $0}' OFS='.')
new_version_with_suffix="$new_version-SNAPSHOT"

echo 'Upgrading version in the build.gralde.kts'
awk -v old="$current_version" -v new="$new_version_with_suffix" '{sub(old, new)}1' build.gradle.kts > build.gradle.kts.tmp && mv build.gradle.kts.tmp build.gradle.kts

echo 'Old version: ' $current_version
echo 'New version: ' $new_version_with_suffix

echo "Building new image | version $new_version_with_suffix"
./gradlew :app:bootBuildImage

echo 'Taging for latest'
docker tag diegolirio/enem-gamification-app:$new_version_with_suffix diegolirio/enem-gamification-app:latest

echo 'Pushing new version '$new_version_with_suffix
docker push diegolirio/enem-gamification-app:$new_version_with_suffix

echo 'Pushing latest version '
docker push diegolirio/enem-gamification-app:latest
