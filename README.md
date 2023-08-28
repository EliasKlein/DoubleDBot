# The DoubleDBot (not really short for DRACOON Discord Bot)

A Bot made over a few days for the unofficial DRACOON Discord Server. And it is my first project I'm actually setting to
public.\
I made it because I wanted to try it out and I didn't want to use some third party bots. (If YOU are reading this: No, I
will not add Carl-Bot. Instead, this Bot will now run on your raspberry pi and hopefully not fry it.)\
If you want to use it feel free. I really don't see the reason why you would want to do so or what is so special about
this Bot. It really can't do much.

## Features

- Self assign roles via reaction (like basically every Bot)
- A new member *experience*
- "Relatively" configurable via property files (within the boundaries planned for the server)
- (optional) Encryption for all properties
    - This is probably very unnecessary but this is a DRACOON Bot.
- Cheer me up command for the darker days

## Good to know stuff

- When using you'll need to add the Discord Bot security token, the encryption password and salt as environment
  variables.
    - The key for the variables can be found and changed in the config.properties.
- Cheer Me Ups - These are properly a little convoluted but bear with me
    - There are 3 kinds: Generic (as many as you like), Special (as many as you like), ultra-rare (only one)
        - All of these are optional, so if you want to use the boring fallback that's ok
        - The ultra-rare always has a chance to trigger over special and generic (currently with a chance of 420 - very
          funny I know, I'm here all night)
        - If the ultra-rare does not trigger the nickname of the user is checked. If it has a special cheer me up
          assigned, it is triggered.
        - Otherwise, a random cheer me up from the generic pool is chosen.
    - A Cheer Me Up can be:
        - A File: Put it into a folder called files in resources and add an optional message as the extra.
        - A Link: A Link. You can also add an extra message here.
        - A Message: Basically works like Link and you could also use Link without using a Link, but that would be
          illegal. Extra is ignored here.
- Theoretically, you can add as many/few games and cheer me ups in the context.properties as you like. Just make sure to
  count the number up properly.
    - This could properly have been done with a DB. See the DB pro and con section for further explanation.
- For the encryption stuff
    - If you want to encrypt any value of the context.properties, add "ENC-" at the start and let the Encryptor do its
      job. All encrypted properties will be in the output.properties from which you can copy them into the
      context.properties. (Writing directly into the context.properties would just butcher any formatting)
    - I was a little inspired by the Jasypt's EncryptableProperties while adding this, which I didn't use as they are
      not part of the light version. This is probably a little hacky, but hey, it just works.
    - Also, I used the pooled encryptor. By default, it only uses a Pool of 1, but if you want to bump it up for the
      placebo speed increase you can do so in the config.properties.

## To DB or not DB

|                               **Pro DB**                                |     **Con DB**      |
|:-----------------------------------------------------------------------:|:-------------------:|
| The Bot does not need to be rebuilt and restarted just to update values | You need a DB setup |
|              Config Files don't contain "production" data               |          -          |
|            Config files don't explode and get very confusing            |          -          |
|                       Literally what DBs are for                        |          -          |

Pretty obvious what the best choice is.

&nbsp;

I don't take feature requests.

Is anyone actually reading this?