package com.example.mpdataprovider.contentprovider.data

class MissingPermissionException(val permissions: List<String>): Exception("Missing permissions :$permissions")

class UpdateException(val rowUpdated: String): Exception("You exceed the allowed update for $rowUpdated")