package eu.kanade.tachiyomi.ui.manga.track

import android.annotation.SuppressLint
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.database.models.Track
import eu.kanade.tachiyomi.data.preference.PreferenceKeys.dateFormat
import eu.kanade.tachiyomi.ui.base.holder.BaseViewHolder
import eu.kanade.tachiyomi.util.view.updateLayoutParams
import eu.kanade.tachiyomi.util.view.visibleIf
import kotlinx.android.synthetic.main.track_item.*

class TrackHolder(view: View, adapter: TrackAdapter) : BaseViewHolder(view) {

    init {
        val listener = adapter.rowClickListener
        logo_container.setOnClickListener { listener.onLogoClick(adapterPosition) }
        add_tracking.setOnClickListener { listener.onSetClick(adapterPosition) }
        track_title.setOnClickListener { listener.onSetClick(adapterPosition) }
        track_remove.setOnClickListener { listener.onRemoveClick(adapterPosition) }
        track_status.setOnClickListener { listener.onStatusClick(adapterPosition) }
        track_chapters.setOnClickListener { listener.onChaptersClick(adapterPosition) }
        score_container.setOnClickListener { listener.onScoreClick(adapterPosition) }
        track_start_date.setOnClickListener { listener.onStartDateClick(adapterPosition) }
        track_finish_date.setOnClickListener { listener.onFinishDateClick(adapterPosition) }
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: TrackItem) {
        val track = item.track
        track_logo.setImageResource(item.service.getLogo())
        logo_container.setBackgroundColor(item.service.getLogoColor())
        logo_container.updateLayoutParams<ConstraintLayout.LayoutParams> {
            bottomToBottom = if (track != null) divider.id else track_details.id
        }
        val serviceName = track_logo.context.getString(item.service.nameRes())
        track_logo.contentDescription = serviceName
        track_group.visibleIf(track != null)
        add_tracking.visibleIf(track == null)
        if (track != null) {

            with(track_title) {
                text = track.title
                isClickable = item.service.isMdList().not()
            }

            track_remove.visibleIf(item.service.isMdList().not())

            with(track_chapters) {
                text = when {
                    track.total_chapters > 0 && track.last_chapter_read == track.total_chapters -> context.getString(
                        R.string.all_chapters_read
                    )
                    track.total_chapters > 0 -> context.getString(
                        R.string.chapter_x_of_y, track.last_chapter_read, track.total_chapters
                    )
                    track.last_chapter_read > 0 -> context.getString(
                        R.string.chapter_, track.last_chapter_read.toString()
                    )
                    else -> context.getString(R.string.not_started)
                }
            }
            val status = item.service.getStatus(track.status)
            if (status.isEmpty()) track_status.setText(R.string.unknown_status)
            else track_status.text = item.service.getStatus(track.status)
            track_score.text = if (track.score == 0f) "-" else item.service.displayScore(track)
            track_score.setCompoundDrawablesWithIntrinsicBounds(0, 0, starIcon(track), 0)
            date_group.visibleIf(item.service.supportsReadingDates)
            if (item.service.supportsReadingDates) {
                track_start_date.text =
                    if (track.started_reading_date != 0L) dateFormat.format(track.started_reading_date) else "-"
                track_finish_date.text =
                    if (track.finished_reading_date != 0L) dateFormat.format(track.finished_reading_date) else "-"
            } else {
            }
        }
    }

    private fun starIcon(track: Track): Int {
        return if (track.score == 0f || track_score.text.toString().toFloatOrNull() != null) {
            R.drawable.ic_star_12dp
        } else {
            0
        }
    }

    fun setProgress(enabled: Boolean) {
        progress.visibleIf(enabled)
    }
}
