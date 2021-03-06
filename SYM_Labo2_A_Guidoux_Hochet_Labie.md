# SYM : Laboratoire 2

## Introduction

## Questions

### Traitement des erreurs

Les interfaces AsyncSendRequest et CommunicationEventListener utilisées au point 3.1 restent très (et certainement trop) simples pour être utilisables dans une vraie application : que se passe-t-il si le serveur n’est pas joignable dans l’immédiat ou s’il retourne un code HTTP d’erreur ? Veuillez proposer une nouvelle version, mieux adaptée, de ces deux interfaces pour vous aider à illustrer votre réponse.

####Réponse

###Authentification

Si une authentification par le serveur est requise, peut-on utiliser un protocole asynchrone ? Quelles seraient les restrictions ? Peut-on utiliser une transmission différée ?

#### Réponse

Non, HTTP est synchrone par définition. Il a besoin d'une confirmation de l'authentification pour continuer le processus. En ce qui concerce les transmissions différées, une persistence des données temporaire serait requise en ce qui concerne les identifiants => Problème de sécurité

###Threads concurrents
Lors de l'utilisation de protocoles asynchrones, c'est généralement deux threads différents qui se préoccupent de la préparation, de l'envoi, de la réception et du traitement des données. Quels problèmes cela peut-il poser ?

#### Réponse

Si un thread se préoccupe de la préparation et l'envoi des données, il pourrait très bien envoyer 2 requêtes à la suite, l'autre thread ne connait pas l'ordre, et un comportement non défini peut se passer.

###Ecriture différée
Lorsque l'on implémente l'écriture différée, il arrive que l'on ait soudainement plusieurs transmissions en attente qui deviennent possibles simultanément. Comment implémenter proprement cette situation (sans réalisation pratique) ? Voici deux possibilités :

- Effectuer une connexion par transmission différée
- Multiplexer toutes les connexions vers un même serveur en une seule connexion de transport. Dans ce dernier cas, comment implémenter le protocole applicatif, quels avantages peut-on espérer de ce multiplexage, et surtout, comment doit-on planifier les réponses du serveur lorsque ces dernières s'avèrent nécessaires ? 

Comparer les deux techniques (et éventuellement d'autres que vous pourriez imaginer) et discuter des avantages et inconvénients respectifs.

#### Réponse

|                       | Avantages                         | Inconvénients |
| --------------------- | --------------------------------- | ------------- |
| Transmission différée |                                   |               |
| Multiplexage          | regroupage des requêtes à l'envoi |               |

###Transmission d’objets
- Quel inconvénient y a-t-il à utiliser une infrastructure de type REST/JSON n'offrant aucun service de validation (DTD, XML-schéma, WSDL) par rapport à une infrastructure comme SOAP offrant ces possibilités ? Est-ce qu’il y a en revanche des avantages que vous pouvez citer ?

  #### Réponse

  | avantages            | inconvienients                  |
  | -------------------- | ------------------------------- |
  | communication légère | Pas d'équivalent à la DTD d'XML |
  | moins verbeux        |                                 |
  |                      |                                 |
  |                      |                                 |

- L’utilisation d’un mécanisme comme Protocol Buffers8 est-elle compatible avec une architecture basée sur HTTP ? Veuillez discuter des éventuelles avantages ou limitations par rapport à un protocole basé sur JSON ou XML ?

  #### Réponse

- Par rapport à l’API GraphQL mise à disposition pour ce laboratoire. Avez-vous constaté des points qui pourraient être améliorés pour une utilisation mobile ? Veuillez en discuter, vous pouvez élargir votre réflexion à une problématique plus large que la manipulation effectuée.

  #### Réponse

###Transmission compressée
Quel gain peut-on constater en moyenne sur des fichiers texte (xml et json sont aussi du texte) en utilisant de la compression du point 3.4 ? Vous comparerez vos résultats par rapport au gain théorique d’une compression DEFLATE, vous enverrez aussi plusieurs tailles de contenu pour comparer.

#### Réponse