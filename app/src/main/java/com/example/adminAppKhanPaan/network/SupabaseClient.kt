import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.auth.Auth

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://jdfhznfgqcgpqjhbymnc.supabase.co",       // from Step 1
        supabaseKey = "sb_publishable_ROACypBBO-SK88JUzjw0eA_a6P4G-AX"           // from Step 1
    ) {
        install(Postgrest)
        install(Storage)
        install(Auth)
    }
}