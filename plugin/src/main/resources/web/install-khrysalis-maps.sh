mkdir node_modules/khrysalis-maps
rsync --delete -azhu ~/IdeaProjects/khrysalis/web/dist node_modules/khrysalis-maps
rsync --delete -azhu ~/IdeaProjects/khrysalis/web/src node_modules/khrysalis-maps
rsync --delete -azhu ~/IdeaProjects/khrysalis/web/package.json node_modules/khrysalis-maps