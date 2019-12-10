from animes.models import Anime
from rest_framework import serializers

class AnimeSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Anime
        fields = ('url', 'titulo', 'genero', 'episodios', 'ano')