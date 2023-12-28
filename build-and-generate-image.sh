
current_version=$(awk -F\" '/version =/ {print $2}' build.gradle.kts)

new_version=$(echo "$current_version" | awk -F. '{$NF+=1; OFS="."; print $0}' OFS='.')
new_version_with_suffix="$new_version-SNAPSHOT"

# changing version in the build.gralde.kts
awk -v old="$current_version" -v new="$new_version_with_suffix" '{sub(old, new)}1' build.gradle.kts > build.gradle.kts.tmp && mv build.gradle.kts.tmp build.gradle.kts

echo 'Old version: ' $current_version
echo 'New version: ' $new_version_with_suffix

./gradlew :app:bootBuildImage

docker tag diegolirio/enem-gamification-app:0.0.1-SNAPSHOT diegolirio/enem-gamification-app:latest

docker push diegolirio/enem-gamification-app:0.0.1-SNAPSHOT
docker push diegolirio/enem-gamification-app:latest