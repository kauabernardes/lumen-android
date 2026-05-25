package org.lumen.app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.lumen.app.R
import org.lumen.app.adapter.PostAdapter
import org.lumen.app.data.model.post.Post
import org.lumen.app.data.model.post.PostUser
import org.lumen.app.databinding.FragmentPostBinding

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        genPosts()
    }

    private fun genPosts() {
        val recycler = binding.recyclerComments
        val postList = listOf(
            Post(
                id = "1001",
                content = "Alguém tem recomendações de livros sobre arquitetura de software? Terminei o Clean Code e estou querendo me aprofundar mais.",
                createdAt = "2024-05-25T10:15:00.000Z",
                user = PostUser("u1", "Ana Silva"),
                isLiked = false,
                likesCount = 42,
                commentsCount = 12
            ),
            Post(
                id = "1002",
                content = "O segredo para não surtar programando é fazer pausas constantes. O método Pomodoro mudou minha vida! 🍅💻",
                createdAt = "2024-05-25T14:30:21.000Z",
                user = PostUser("u2", "DevCansado"),
                isLiked = true,
                likesCount = 156,
                commentsCount = 34
            ),
            Post(
                id = "1003",
                content = "Hoje finalmente consegui resolver aquele bug que me assombrava há 3 dias. A sensação é indescritível.",
                createdAt = "2024-05-26T09:05:11.000Z",
                user = PostUser("1234", "Kauã"),
                isLiked = true,
                likesCount = 89,
                commentsCount = 5
            ),
            Post(
                id = "1004",
                content = "Kotlin > Java. Pronto, falei.",
                createdAt = "2024-05-26T18:45:00.000Z",
                user = PostUser("u4", "Pedro_Android"),
                isLiked = false,
                likesCount = 205,
                commentsCount = 98
            ),
            Post(
                id = "1005",
                content = "Testando o layout com um texto um pouco maior para ver como o RecyclerView se comporta. É muito importante garantir que textos longos não quebrem a interface e que o wrap_content do card esteja funcionando perfeitamente no XML. Se você está lendo isso, seu layout está ótimo!",
                createdAt = "2024-05-27T11:20:33.000Z",
                user = PostUser("u5", "QA_Tester"),
                isLiked = false,
                likesCount = 12,
                commentsCount = 0
            ),
            Post(
                id = "1006",
                content = "Alguém mais vai participar do evento do Google I/O este ano?",
                createdAt = "2024-05-28T08:00:00.000Z",
                user = PostUser("12345", "Kauã Berdadeds"),
                isLiked = true,
                likesCount = 33,
                commentsCount = 7
            )
        )

        val postAdapter = PostAdapter(postList)

        val dividerItemDecoration = DividerItemDecoration(
            recycler.context,
            recycler.resources.configuration.orientation
        )
        recycler.addItemDecoration(dividerItemDecoration)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.setHasFixedSize(true)
        recycler.adapter = postAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}