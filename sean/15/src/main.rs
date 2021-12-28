// _Now_ we can do some good ol' graph theory!

use std::fs;

use petgraph::Graph;
use petgraph::graph::NodeIndex;
use petgraph::algo::astar;

/// Find and print the solution.
pub fn solve(part: u8) {
    // Convert the input into a graph
    let matrix = parse_input_into_matrix("input.txt", part);
    let graph = create_graph_from_matrix(&matrix);

    // Find the shortest path from the first to the last node (i.e. the top-left to the bottom-right location)
    let start = NodeIndex::new(0);
    let finish = NodeIndex::new(coords_to_id(matrix[0].len() - 1, matrix.len() - 1, matrix[0].len()) as usize);
    let path = astar(&graph, start, |node| node == finish, |edge| *edge.weight(), |_| 0).unwrap();

    // Print the path's length
    println!("Part {} answer: {}", part, path.0);
}

/// Return the contents of input.txt as a matrix.
fn parse_input_into_matrix(file_name: &str, part: u8) -> Vec<Vec<u32>> {
    // Split the input text into a matrix of numbers
    let mut matrix: Vec<Vec<u32>> = vec![];
    let input_string = fs::read_to_string(file_name).unwrap();
    let input_lines: Vec<_> = input_string.split_terminator("\n").collect();
    for line in input_lines {
        let line_as_chars: Vec<_> = line.trim().split("").filter(|&s| !s.is_empty()).collect();
        let line_as_nums: Vec<u32> = line_as_chars.iter().map(|s| s.parse::<u32>().unwrap()).collect();
        matrix.push(line_as_nums);
    }

    // For part 2, extend the matrix by duplicating it row-by-row and adjusting the risk levels
    if part == 2 {
        let input_matrix_size = matrix.len();
        for n in 1..5 {
            // Extend downward
            for y in 0..input_matrix_size {
                matrix.extend_from_within(y..=y); // Add a copy of this row to the bottom
                let last_row_index = matrix.len() - 1; // Save this off so we don't have to borrow matrix twice
                increment_risk_levels(&mut matrix[last_row_index][..], n); // Adjust risk levels of the new slice
            }
        }
        for n in 1..5 {
            // Extend rightward
            for y in 0..matrix.len() {
                matrix[y].extend_from_within(0..input_matrix_size); // Add a copy of the original row to the right
                let starting_index = matrix[y].len() - input_matrix_size; // Get the start of the new slice
                increment_risk_levels(&mut matrix[y][starting_index..], n); // Adjust risk levels of the new slice
            }
        }
    }

    matrix
}

/// Given a vector of risk levels, increment each one in place, wrapping those that go past 9 back to 1.
fn increment_risk_levels(slice: &mut [u32], increment_by: u32) {
    for i in 0..slice.len() {
        slice[i] += increment_by;
        if slice[i] > 9 {
            slice[i] %= 9;
        }
    }
}

/// From a matrix of risk levels, create a directed graph whose nodes represent locations in the matrix and whose
/// edges represent the amount of risk added when moving between the corresponding locations.
fn create_graph_from_matrix(matrix: &Vec<Vec<u32>>) -> Graph<(), u32> {
    // Create a list of edges between locations, where weight is the destination's risk level
    let mut edges: Vec<_> = Vec::new();
    for y in 0..matrix.len() {
        let matrix_width = matrix[y].len();
        for x in 0..matrix_width {
            let here = coords_to_id(x, y, matrix.len());
            if x > 0                { edges.push((here, coords_to_id(x - 1, y, matrix_width), matrix[x - 1][y])) }
            if y > 0                { edges.push((here, coords_to_id(x, y - 1, matrix_width), matrix[x][y - 1])) }
            if x < matrix_width - 1 { edges.push((here, coords_to_id(x + 1, y, matrix_width), matrix[x + 1][y])) }
            if y < matrix_width - 1 { edges.push((here, coords_to_id(x, y + 1, matrix_width), matrix[x][y + 1])) }
        }
    }

    // Turn the list of edges into a graph
    Graph::<(), u32>::from_edges(&edges)
}

/// Given a pair of coordinates and the width of their containing matrix, return a unique identifier for the
/// specified location.
fn coords_to_id(x: usize, y: usize, width: usize) -> u32 {
    (width * y + x) as u32
}

fn main() {
    for part in 1..=2 { // Teehee
        solve(part);
    }
}
