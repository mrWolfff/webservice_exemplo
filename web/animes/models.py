from django.db import models

class Anime(models.Model):
    url = models.CharField(max_length=250)
    titulo = models.CharField(max_length=50)
    genero = models.CharField(max_length=50)
    episodios = models.IntegerField()
    ano = models.IntegerField()
    
    def __str__(self):
        return self.titulo
