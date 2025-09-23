import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gamebugs.R
import com.example.gamebugs.ui.components.RegistrationPanel
import com.example.gamebugs.ui.theme.GameBugsTheme

@Composable
fun MainMenuPanel(){
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Регистрация", "Профиль", "Настройки")

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 7.dp, vertical = 45.dp)
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Левая колонка с "вкладками"
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(vertical = 20.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Text(
                        text = title,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { selectedTab = index },
                        style = if (selectedTab == index) {
                            MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                        } else {
                            MaterialTheme.typography.bodyLarge
                        }
                    )
                }
            }

            // Контент справа
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(10.dp)
            ) {
                when (selectedTab) {
                    0 -> RegistrationPanel()
                    1 -> Text("Здесь будет профиль")
                    2 -> Text("Здесь будут настройки")
                }
            }
        }
    }

}

@Preview
@Composable
fun PreviewMainMenuPanel(){
    GameBugsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainMenuPanel()
        }
    }
}