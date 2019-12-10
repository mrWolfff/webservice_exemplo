from django.shortcuts import render
from rest_framework import viewsets
from rest_framework import permissions
from animes.models import Anime
from animes.serializers import AnimeSerializer

class AnimeViewSet(viewsets.ModelViewSet):
    queryset = Anime.objects.all()
    serializer_class = AnimeSerializer

def index(request):
    return request(request, 'animes/index.html', {})
