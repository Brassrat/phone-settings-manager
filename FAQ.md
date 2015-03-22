#UNDER CONSTRUCTION.

# Introduction #

The ProfileManager application supports creation of sets of Android  attributes. Each set of attributes (aka profile) can have a list of times for the attribute setting to be activated.

This application was derived from the SoundManager application (app-soundmanager project).
However the data model is somewhat different. Instead of attaching a time schedule to each attribute I have added the concept of a profile which has settings for 0 or more attributes and 0 or more schedule times. Thus a single profile can modify all of the available settings. Profiles can be manually applied. Individual profiles can be disabled (e.g., disable your work profile while on vacation) and alternatively, all scheduled setting changes can be temporarily inhibited (i.e., while at a concert.)
Also, support for additional attributes beyond the basic sound settings have been added.

The goal is to add support for additional attributes based upon a JSON description, but currently there are only 2 explicit java-based classes of attributes,
(1) sound attributes which support a seek bar for setting their value and where appropriate a boolean for vibration enable/disable
(2) xmit attributes which only support a boolean attribute to enable/disable.

The current 'xmit' attributes are 'AirPlane' and 'Wifi' which enable/disable the associated phone setting.

Attribute descriptions (aka registrations) are contained in a database table.
The goal is to be able to add additional attribute registrations, using additional Java-based classes and eventually 'Attribute Intent's will provide the necessary semantics.


# Details #

Add your content here.  Format your content with:
  * Text in **bold** or _italic_
  * Headings, paragraphs, and lists
  * Automatic links to other wiki pages