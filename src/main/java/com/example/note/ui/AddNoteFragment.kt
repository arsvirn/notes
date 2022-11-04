package com.example.note.ui

import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.note.R
import com.example.note.databinding.FragmentAddNoteBinding
import com.example.note.db.Note
import com.example.note.db.NoteDatabase
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.launch

class AddNoteFragment : BaseFragment() {

    private var note: Note? = null

    lateinit var binding: FragmentAddNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            note = AddNoteFragmentArgs.fromBundle(it).note
            et_text_title.setText(note?.title)
            et_text_note.setText(note?.note)
        }

        btn_save.setOnClickListener {

            val noteTitle = et_text_title.text.toString().trim()
            val noteBody = et_text_note.text.toString().trim()

            if(noteTitle.isEmpty()){
                et_text_title.error = "title required"
                et_text_title.requestFocus()
                return@setOnClickListener
            }
            if(noteBody.isEmpty()){
                et_text_note.error = "note required"
                et_text_note.requestFocus()
                return@setOnClickListener
            }

            launch {
                context?.let {

                    val mNote = Note(noteTitle, noteBody)

                    if(note == null) {
                        val directions = AddNoteFragmentDirections.actionAddNoteFragmentToHomeFragment()
                        findNavController().navigate(directions)
                        NoteDatabase(it).getNoteDao().addNote(mNote)
                        it.toast("Note Saved")
                    }
                    else {
                        val directions = AddNoteFragmentDirections.actionAddNoteFragmentToHomeFragment()
                        findNavController().navigate(directions)
                        mNote.id = note!!.id
                        NoteDatabase(it).getNoteDao().updateNote(mNote)
                        it.toast("Note Updated")
                    }
                    }
                }
            }
        }

    private fun deleteNote(){
        AlertDialog.Builder(context).apply {
            setTitle("Are you sure?")
            setMessage("You cannot undo this operation")
            setPositiveButton("Yes") { _, _ ->
                launch {
                    NoteDatabase(context).getNoteDao().deleteNote(note!!)
                    val directions = AddNoteFragmentDirections.actionAddNoteFragmentToHomeFragment()
                    findNavController().navigate(directions)
                }
            }
            setNegativeButton("No") { _, _ ->

            }
        }.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete -> if(note != null) deleteNote() else context?.toast("Cannot Delete")
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }
    }