package com.example.mpdataprovider.ContentProvider.data

class MissingPermissionException(val permissions: List<String>): Exception("Missing permissions :$permissions")

class UpdateException(val rowUpdated: String): Exception("You exceed the allowed update for $rowUpdated")