## pokemon-classifier
# Pokemon Classifier ML project



## CS 185 C Fall 2016 - Project Proposal

# Joshua Zheng
# Sidharth Mishra
# Wei Chung Huang 
# Instructor - James Casaletto 
# CS 185 C, Fall 2016 November 14, 2016


# Group Membership :
* Joshua Zheng
* Sidharth Mishra
* Wei Chung Huang

We plan to use the following datasets from kaggle.com:

• https://www.kaggle.com/abcsds/pokemon
• https://www.kaggle.com/kveykva/sf-bay-area-pokemon-go-spawns • https://www.kaggle.com/abcsds/pokemongo

We plan on using data from the websites below for supporting our classiﬁcations:

• http://www.pokemon.com/us/pokedex/ • http://pokemondb.net/pokedex • http://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9-mon_by_National_Pok%C3%A9dex_number

We plan to take up one module each.

The description of the modules are as follows:

# Module 1: [ To be taken up by Wei Chung ] :

We want to classify pokemons relative to a given defending pokemon. For example, we could deﬁne 4 scales of pokemon like "weak", "decent", "strong" and “amazing”. Since in our dataset we do not have these labels, we plan to use unsu-pervised learning such as K-Means and GMM. However, the challenge could be how to deﬁne these scales and which features to use. Let’s suppose the input pokemon has many ﬁelds but our user just wants to know, is the pokemon say, Bulbasaur stronger than Squirtle? So, by classifying the pokemons we plan to recommend pokemons best ﬁtting the user’s preferences.

# Module 2: [To be taken up by Joshua]:
This module will use the ﬁrst dataset (https://www.kaggle.com/abcsds/pokemon). We plan to ﬁnd the best possible options to defeat the pokemon so that the user can make the best choice of pokemon in the upcoming battle through comparing each pokemon with their type and stat so that we can visualize which options would be best to beat an opposing pokemon. With the data, we will be able to assist the trainer to make a stronger team that can be used to counter as many opponents as possible as a pokemon strategy.

# Module 3: [To be taken up by Sidharth]:
Given the location of the user (latitude, longitude) and Pokemon type, we recommend pokemons (and their spawn spots) that have spawned nearby keeping the highest ranked one on the top of the list. Other facets include the minimum Combat Power or minimum HP or both. This module will use data from all three datasets. We plan to leverage the results from the ﬁrst and second modules as well if time permits to further reﬁne the recommendation of the pokemon and improve the ranking of the pokemons in the recommendations.

# Components to be used:

We plan on using MLib and GraphX libraries and DataFrames and Datasets of Spark.

