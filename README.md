# The DoubleDBot (not really short for DRACOON Discord Bot)

A Bot made in 1 and a half days for the unofficial DRACOON Discord Server.
And it is my first project I'm actually setting to public.\
I made it because I wanted to try it out and I didn't want to use some third party bots.
(If YOU are reading this: No, I will not add Carl-Bot. 
Instead, this Bot will now run on your raspberry pi and hopefully not fry it.)\
If you want to use it feel free. I really don't see the reason why you would want to do so
or what is so special about this Bot. It really can't do much.

## Features

- Self assign roles via reaction (like basically every Bot)
- A new member *experience*
- "Relatively" configurable via property files (within the boundaries planned for the server)
- Encryption for role and channel IDs
  - This is probably very unnecessary but this is a DRACOON Bot.
  - It basically only makes it harder add stuff in the context.properties, but it is like it is.
  - I could probably try to do something that makes it not shit to use, but it is working for me, so I'm happy.

## Good to know stuff

- When using you'll need to add the Discord Bot security token, the encryption password and salt as environment variables.
  - The key for the variables can be found and changed in the config.properties.
- Theoretically, you can add as many/few games in the context.properties as you like. Just make sure to count the number up properly.
- For the encryption stuff
  - All IDs are and need to be encrypted with the given pw and salt.
  - For everything else I used the default configuration of Jasypt's PooledPBEStringEncryptor
  (In case you actually want to use it and therefore need to change the encrypted IDs)
  - And I used the pooled encryptor. By default, it only uses a Pool of 1,
  but if you want to bump it up for the placebo speed increase you can do so in the config.properties.

I don't take feature requests.

Is anyone actually reading this?