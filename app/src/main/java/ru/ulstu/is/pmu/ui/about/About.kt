package ru.ulstu.`is`.pmu.ui.about

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import ru.ulstu.`is`.pmu.R
import ru.ulstu.`is`.pmu.ui.theme.PmudemoTheme

@Composable
fun About() {
    val localContext = LocalContext.current
    val aboutText = localContext.resources.getText(R.string.about_text)

    val urlOnClick = {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse("https://ulstu.ru/")
        localContext.startActivity(openURL)
    }

    Column(Modifier.padding(all = 10.dp)) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = urlOnClick),
            factory = { context -> TextView(context) },
            update = { it.text = aboutText }
        )
        Spacer(Modifier.padding(bottom = 10.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = urlOnClick
        ) {
            Text(stringResource(id = R.string.about_title))
        }
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AboutPreview() {
    PmudemoTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            About()
        }
    }
}