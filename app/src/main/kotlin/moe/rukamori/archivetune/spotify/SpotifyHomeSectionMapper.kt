/*
 * YumaPlayer (2026) | Modified work by MuwMix
 * ArchiveTune (2026) | Original work by © Rukamori
 * GPL-3.0 License | Contributors: see git history
 */

package moe.rukamori.archivetune.spotify

import moe.rukamori.archivetune.spotify.models.SpotifyAlbum
import moe.rukamori.archivetune.spotify.models.SpotifyArtist
import moe.rukamori.archivetune.spotify.models.SpotifyHomeFeedItem
import moe.rukamori.archivetune.spotify.models.SpotifyHomeFeedSection
import moe.rukamori.archivetune.spotify.models.SpotifyImage
import moe.rukamori.archivetune.spotify.models.SpotifyPlaylist
import moe.rukamori.archivetune.spotify.models.SpotifyPlaylistOwner
import moe.rukamori.archivetune.spotify.models.SpotifyPlaylistTracksRef

/**
 * Convert every content type returned in a Spotify home shelf.
 *
 * Some Spotify feed responses mix playlists, albums, and artists in a single
 * section. Keeping only the dominant type silently discards the other items,
 * so split mixed shelves into smaller typed shelves instead.
 */
internal fun convertHomeSections(feedSection: SpotifyHomeFeedSection): List<SpotifyHomeSection> {
    val title = feedSection.title ?: return emptyList()

    val playlists = feedSection.items.filterIsInstance<SpotifyHomeFeedItem.Playlist>()
    val albums = feedSection.items.filterIsInstance<SpotifyHomeFeedItem.Album>()
    val artists = feedSection.items.filterIsInstance<SpotifyHomeFeedItem.Artist>()
    val populatedTypes = listOf(playlists.isNotEmpty(), albums.isNotEmpty(), artists.isNotEmpty()).count { it }

    fun sectionTitle(suffix: String) =
        if (populatedTypes > 1) "$title · $suffix" else title

    return buildList {
        if (playlists.isNotEmpty()) {
            add(
                SpotifyHomeSection(
                    title = sectionTitle("Playlists"),
                    type = SectionType.PLAYLISTS,
                    playlists = playlists.map {
                        SpotifyPlaylist(
                            id = it.id,
                            name = it.name,
                            description = it.description,
                            images = listOfNotNull(it.imageUrl?.let { url -> SpotifyImage(url, null, null) }),
                            owner = it.ownerName?.let { owner -> SpotifyPlaylistOwner(id = "", displayName = owner) },
                            tracks = SpotifyPlaylistTracksRef(total = it.totalCount),
                            uri = it.uri,
                        )
                    },
                ),
            )
        }
        if (albums.isNotEmpty()) {
            add(
                SpotifyHomeSection(
                    title = sectionTitle("Albums"),
                    type = SectionType.ALBUMS,
                    albums = albums.map {
                        SpotifyAlbum(
                            id = it.id,
                            name = it.name,
                            albumType = it.albumType,
                            artists = it.artists,
                            images = listOfNotNull(it.imageUrl?.let { url -> SpotifyImage(url, null, null) }),
                            uri = it.uri,
                        )
                    },
                ),
            )
        }
        if (artists.isNotEmpty()) {
            add(
                SpotifyHomeSection(
                    title = sectionTitle("Artists"),
                    type = SectionType.ARTISTS,
                    artists = artists.map {
                        SpotifyArtist(
                            id = it.id,
                            name = it.name,
                            images = listOfNotNull(it.imageUrl?.let { url -> SpotifyImage(url, null, null) }),
                            uri = it.uri,
                        )
                    },
                ),
            )
        }
    }
}
