VoxelSniper [![Build Status](https://travis-ci.org/TVPT/VoxelSniper.svg?branch=master)](https://travis-ci.org/TVPT/VoxelSniper)
===============

The premiere long-distance brush editor implementation for [Bukkit][Bukkit] and [Sponge][Sponge]. Documentation can be found at the [VoxelGunsmith wiki][VoxelSniperWiki].

Compilation
-----------

We use [Gradle][Gradle] for dependencies and building.

- Check out this repository.
- Run ```./gradlew```

The master branch is automatically build on our jenkins server ([VoxelSniper Jenkins Job][JenkinsJob]).

Issue Tracker Notes
-------------------

How do I create a ticket the right way?

- Seperate your reports. You think there is something wrong, but also want this new brush? Make life easier for us and create two tickets. We'd appriciate it big times.
- Don't tell us your story of life. We want facts and information. The more information about `the Problem` you give us, the easier it is for us to figure out what's wrong.
- Check the closed tickets first. Maybe someone created a similiar ticket already. If you think it's unresolved, then give us more information on there instead.


### Enhancement Request

This is where imagination comes in, but make sure to keep as it easy for us. As mentioned, we don't want your story of life. We want to know what you think would be a good enhancement.

Here is an example of an enhancement request.

Title: `Brush that creates lines`

```
Enhancement Proposal:
Creating a brush that creates a line.

Suggested usage:
You click two points with your arrow and it will create a line with blocks.

Reason of proposal:
It would be useful, since off angle lines are sometimes hard to make.
```

Keep in mind that those are guidelines.
We will still look into stuff that does not follow these guidlines, but it will give you an idea what we want in a ticket and make our life easier.

Pull Requests
-------------

We do accept pull requests and enhancements from third parties. Guidelines how to submit your pull requests properly and how to format your code will come.

Some rough guidelines for now:

- Keep the number of commits to a minimum. We want to look over the commit and basically see what you've done.
- Coding guidelines should try to comply to the checkstyle rules (checkstyle.xml) but not blindly. Use your mind to make smart decissions.
- Give us a good description to what you've done.
- Try to submit one change in one pull request and try to link it to the issue in the tracker if possible.

[VoxelSniperWiki]: https://github.com/TVPT/VoxelGunsmith/wiki
[JenkinsJob]: http://ci.voxelmodpack.com/view/VoxelSniper/
[Bukkit]: http://bukkit.org/
[Sponge]: https://spongepowered.org/
[Gradle]: https://www.gradle.org
