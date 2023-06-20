.. _kotlin-upgrade-driver:

=======================
Upgrade Driver Versions
=======================

.. default-domain:: mongodb

.. contents:: On this page
   :local:
   :backlinks: none
   :depth: 1
   :class: singlecol


Overview
--------

In this section, you can identify the changes you need to make to your
application to upgrade your driver to a new version.

Before you upgrade, perform the following actions:

- Ensure the new version is compatible with the {+mdb-server+} versions
  your application connects to and the Java Runtime Environment (JRE) your
  application runs on. See the :ref:`Kotlin Compatibility <kotlin-compatibility-tables>`
  page for this information.
- Address any breaking changes between the current version of the driver
  your application is using and your planned upgrade version in the
  :ref:`Breaking Changes <kotlin-breaking-changes>` section. To learn
  more about the {+mdb-server+} release compatibility changes, see the
  :ref:`<kotlin-server-release-changes>` section.

.. tip::

   To minimize the amount of changes your application may require when
   upgrading driver versions in the future, use the
   :ref:`{+stable-api+} <stable-api-kotlin>`.

.. _kotlin-breaking-changes:

Breaking Changes
----------------

A breaking change is a modification in a convention or behavior in
a specific version of the driver that may prevent your application from
working properly if not addressed before upgrading.

The breaking changes in this section are categorized by the driver version that
introduced them. When upgrading driver versions, address all the breaking
changes between the current and upgrade versions. For example, if you
are upgrading the driver from v4.0 to v4.5, address all breaking changes from
the version after v4.0 including any listed under v4.5.

.. _kotlin-breaking-changes-v4.10:

Version 4.10 Breaking Changes
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

There are no breaking changes in this version.

.. _kotlin-server-release-changes:

Server Release Compatibility Changes
------------------------------------

A server release compatibility change is a modification
to the {+driver-long+} that discontinues support for a set of
{+mdb-server+} versions.

The driver discontinues support for a {+mdb-server+} version after it reaches
end-of-life (EOL).

To learn more about the MongoDB support for EOL products,
see the `Legacy Support Policy <https://www.mongodb.com/support-policy/legacy>`__.
